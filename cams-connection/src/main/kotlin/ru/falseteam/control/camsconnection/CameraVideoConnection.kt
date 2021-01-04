package ru.falseteam.control.camsconnection

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import ru.falseteam.control.camsconnection.protocol.CommandCode
import ru.falseteam.control.camsconnection.protocol.CommandRepository

internal class CameraVideoConnection(private val address: String, private val port: Int) :
    AbstractCameraConnection(address, port) {
    public override val connectionObservable: Flow<CameraConnectionState>
        get() = super.connectionObservable.map {
            if (it is CameraConnectionState.AbstractConnected) {
                CameraConnectionState.ConnectedVideo(setupVideoEvent(it))
            } else it
        }
            .flowOn(Dispatchers.IO)

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