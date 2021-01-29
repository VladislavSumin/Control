package ru.falseteam.control.camsconnection

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.plus
import ru.falseteam.control.camsconnection.protocol.CommandCode
import ru.falseteam.control.camsconnection.protocol.CommandRepository

internal class CameraVideoConnection(private val address: String, private val port: Int) :
    AbstractCameraConnection(address, port) {

    val observeVideoStream: Flow<ByteArray> =
        connectionObservable.filter {
            when (it) {
                is CameraConnectionState.Connecting -> false
                is CameraConnectionState.AbstractConnected -> true
                is CameraConnectionState.Disconnected -> throw it.exception
                else -> throw Exception("Unexpected connection state: $it")
            }
        }
            .map { it as CameraConnectionState.AbstractConnected }
            .flatMapLatest { setupVideoEvent(it) }
            .shareIn(
                GlobalScope + CoroutineName("camera_video_connection_${address}_$port"),
                SharingStarted.WhileSubscribed(replayExpirationMillis = 0)
            )

    private fun setupVideoEvent(
        state: CameraConnectionState.AbstractConnected,
    ) = state.receive
        .filter { it.messageId == CommandCode.MONITOR_DATA }
        .map { it.data }
        .onStart {
            log.debug("Requesting video stream from $address:$port")
            state.send(CommandRepository.monitorClaim(state.sessionId))
            state.send(CommandRepository.monitorStart(state.sessionId))
        }
}