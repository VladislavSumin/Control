package ru.falseteam.control.camsconnection

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.channelFlow
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.InetSocketAddress
import java.util.concurrent.CancellationException

class CameraConnection(
    private val address: String,
    private val port: Int,
) {
    companion object {
        private val log = LoggerFactory.getLogger("cams.connection")
    }

    val connectionObservable = channelFlow {
        while (true) {
            send(CameraConnectionState.Connecting)
            var client: Socket? = null
            try {
                client = connect()
                send(CameraConnectionState.Connected)
                client.awaitClosed()
                throw IOException("connection closed")
            } catch (e: CancellationException) {
                log.trace("Connection with $address:$port closed. (Coroutine cancelled)")
                throw e
            } catch (e: Exception) {
                log.trace("Connection with $address:$port closed or not established with exception: ${e.message}")
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

    private suspend fun connect(): Socket {
        log.trace("Connecting to $address:$port")
        val socket = aSocket(ActorSelectorManager(Dispatchers.IO))
            .tcp()
            .connect(InetSocketAddress(address, port))
        log.trace("Connection with $address:$port established")
        return socket
    }
}