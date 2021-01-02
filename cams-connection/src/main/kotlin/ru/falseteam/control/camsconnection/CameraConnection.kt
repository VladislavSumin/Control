package ru.falseteam.control.camsconnection

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.slf4j.LoggerFactory
import ru.falseteam.control.camsconnection.protocol.CommandCode
import ru.falseteam.control.camsconnection.protocol.CommandRepository
import ru.falseteam.control.camsconnection.protocol.Msg
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.CancellationException

class CameraConnection(
    private val address: String,
    private val port: Int,
) {
    companion object {
        private val log = LoggerFactory.getLogger("cams.connection")
    }

    private val writeLock = Mutex()

    val connectionObservable = channelFlow {
        while (true) {
            send(CameraConnectionState.Connecting)
            var clientGlobal: Socket? = null
            try {
                coroutineScope {
                    val client = connect()
                    clientGlobal = client

                    val writeChannel = client.openWriteChannel()
                    val readChannel = client.openReadChannel()
                    val sessionId = auth(readChannel, writeChannel)
                    log.trace("Auth complete for connection $address:$port, sessionId=$sessionId")
                    val receiveFlow = makeReceiveFlow(readChannel, this)
                    launch { ping(writeChannel, sessionId) }
                    send(CameraConnectionState.Connected(receiveFlow) { writeChannel.write(it) })
                    client.awaitClosed()
                    throw IOException("connection closed by camera")
                }
            } catch (e: CancellationException) {
                log.trace("Connection with $address:$port closed. (Coroutine cancelled)")
                throw e
            } catch (e: Exception) {
                log.trace("Connection with $address:$port closed or not established with exception: ${e.message}")
                send(CameraConnectionState.Disconnected(e))
            } finally {
                try {
                    clientGlobal?.close()
                } catch (e: Exception) {
                    log.warn("Error when closing connection with $address:$port", e)
                }
            }
            delay(5000)
            log.trace("Reconnecting to $address:$port")
        }
    }
        .flowOn(Dispatchers.IO)
        .shareIn(GlobalScope, SharingStarted.WhileSubscribed(replayExpirationMillis = 0), 1)

    //TODO research maybe ping not need if auth return AliveInterval = 0
    private suspend fun ping(write: ByteWriteChannel, sessionId: Int) {
        ticker(10000, 2000).receiveAsFlow().collect {
            log.trace("Ping $address:$port")
            write.write(CommandRepository.keepAlive(sessionId))
        }
    }

    private fun makeReceiveFlow(read: ByteReadChannel, scope: CoroutineScope) = flow {
        while (true) {
            val msg = read.readMsg()
            if (msg.messageId == CommandCode.KEEPALIVE_RSP) continue
            emit(msg)
        }
    }.shareIn(scope, SharingStarted.Eagerly)

    private suspend fun auth(read: ByteReadChannel, write: ByteWriteChannel): Int {
        val msg = CommandRepository.auth()
        write.write(msg)
        val response = read.readMsg()
        //FIXME add response code parsing, to check wrong auth()
        if (response.messageId != CommandCode.LOGIN_RSP) throw IOException("Auth failed")
        return response.sessionId
    }

    private suspend fun connect(): Socket {
        log.trace("Connecting to $address:$port")
        val socket = aSocket(ActorSelectorManager(Dispatchers.IO))
            .tcp()
            .connect(InetSocketAddress(address, port))
        log.trace("Connection with $address:$port established")
        return socket
    }

    private suspend fun ByteWriteChannel.write(msg: Msg) {
        writeLock.withLock {
            log.trace("Send ${msg.messageId.name} to $address:$port")

            val buffer = ByteBuffer.allocate(msg.getSize())
            buffer.order(ByteOrder.LITTLE_ENDIAN)
            with(buffer) {
                put(msg.headFlag)
                put(msg.version)
                put(msg.reserved01)
                put(msg.reserved02)
                putInt(msg.sessionId)
                putInt(msg.sequenceNumber)
                put(msg.totalPacket)
                put(msg.currentPacket)
                putShort(msg.messageId.code.toShort())
                putInt(msg.dataLength)
                put(msg.data)

                flip()
            }
            writeFully(buffer)
            flush()
        }
    }

    private suspend fun ByteReadChannel.readMsg(): Msg {
        with(Msg()) {
            var buffer = ByteBuffer.allocate(20)
            buffer.order(ByteOrder.LITTLE_ENDIAN)
            readFully(buffer)
            buffer.flip()

            headFlag = buffer.get()
            version = buffer.get()
            reserved01 = buffer.get()
            reserved02 = buffer.get()
            sessionId = buffer.int
            sequenceNumber = buffer.int
            totalPacket = buffer.get()
            currentPacket = buffer.get()
            val tmp = buffer.short.toInt()
            messageId = CommandCode.fromCode(tmp)

            dataLength = buffer.int
            buffer = ByteBuffer.allocate(dataLength)
            readFully(buffer)
            buffer.flip()
            data = buffer.array()

            log.trace("Received ${this.messageId.name} from $address:$port")
            return this
        }
    }
}