package ru.falseteam.control.camsconnection

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import ru.falseteam.control.camsconnection.protocol.CommandCode
import ru.falseteam.control.camsconnection.protocol.CommandRepository

class CameraConnection(private val address: String, private val port: Int) :
    AbstractCameraConnection(address, port) {
    private val cameraVideoConnection by lazy { CameraVideoConnection(address, port) }

    public override val connectionObservable: Flow<CameraConnectionState>
        get() = super.connectionObservable.map {
            if (it is CameraConnectionState.AbstractConnected) {
                CameraConnectionState.Connected(setupMovementEvent(it))
            } else it
        }
            .shareIn(GlobalScope, SharingStarted.WhileSubscribed(replayExpirationMillis = 0), 1)

    public val videoObservable: Flow<ByteArray> = cameraVideoConnection.observeVideoStream


    private fun setupMovementEvent(
        state: CameraConnectionState.AbstractConnected,
    ) = state.receive
        .filter { it.messageId == CommandCode.ALARM_REQ }
        .map { }
        .onStart {
            log.debug("Requesting movement event from $address:$port")
            state.send(CommandRepository.alarmStart(state.sessionId))
        }
        .shareIn(state.connectionScope, SharingStarted.Lazily)
}