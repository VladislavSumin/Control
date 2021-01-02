package ru.falseteam.control.camsconnection

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.channelFlow
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.channels.AsynchronousSocketChannel
import java.util.concurrent.CancellationException
import java.util.concurrent.Future

class CameraConnection(
    private val address: String,
    private val port: Int,
) {
    companion object {
        private val log = LoggerFactory.getLogger("cams.connection")
    }

    val connectionObservable = channelFlow<CameraConnectionState> {
        while (true) {
            send(CameraConnectionState.Connecting)
            var client: AsynchronousSocketChannel? = null
            try {
                client = connect()
                send(CameraConnectionState.Connected)

                while (client.isOpen) {
                    delay(300)
                }
                throw IOException("connection closed")
            } catch (e: CancellationException) {
                log.trace("Connection with $address:$port closed. (Coroutine cancelled)")
                throw e
            } catch (e: Exception) {
                send(CameraConnectionState.Disconnected(e))
            } finally {
                try {
                    client?.close()
                } catch (e: Exception) {
                    log.warn("Error when closing connection with $address:$port", e)
                }
            }
            delay(5000)
            log.trace("Reconnecting to $address:$port")
        }
    }

    private suspend fun connect(): AsynchronousSocketChannel {
        log.trace("Connecting to $address:$port")
        val client = AsynchronousSocketChannel.open()
        val socketAddress = InetSocketAddress(address, port)
        client.connect(socketAddress).await()
        log.trace("Connection with $address:$port established")
        return client
    }

    private suspend fun <T> Future<T>.await(): T {
        return try {
            while (!isDone) delay(2)
            get()
        } catch (e: CancellationException) {
            cancel(false)
            throw e
        }
    }
}