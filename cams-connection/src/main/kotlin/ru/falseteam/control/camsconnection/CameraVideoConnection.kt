package ru.falseteam.control.camsconnection

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import ru.falseteam.control.camsconnection.protocol.CommandCode
import ru.falseteam.control.camsconnection.protocol.CommandRepository

class CameraVideoConnection(address: String, port: Int) : AbstractCameraConnection(address, port) {
    public override val connectionObservable: SharedFlow<CameraConnectionState>
        get() = super.connectionObservable.map {
            if (it is CameraConnectionState.AbstractConnected) {
                CameraConnectionState.ConnectedVideo(setupMovementEvent(it))
            } else it
        }
            .flowOn(Dispatchers.IO)
            .shareIn(GlobalScope, SharingStarted.WhileSubscribed(replayExpirationMillis = 0), 1)


    private fun setupMovementEvent(
        state: CameraConnectionState.AbstractConnected,
    ) = state.receive
        .filter { it.messageId == CommandCode.MONITOR_DATA }
        .map { it.data }
        .onStart {
            state.send(CommandRepository.monitorClaim(state.sessionId))
            state.send(CommandRepository.monitorStart(state.sessionId))
        }
        .shareIn(state.connectionScope, SharingStarted.Lazily)
}