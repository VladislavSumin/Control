package ru.falseteam.control.camsconnection

import io.ktor.network.selector.*
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.plus
import ru.falseteam.control.camsconnection.protocol.CommandCode
import ru.falseteam.control.camsconnection.protocol.CommandRepository

class CameraConnection(
    private val selector: SelectorManager,
    private val address: String,
    private val port: Int
) :
    AbstractCameraConnection(selector, address, port) {
    private val cameraVideoConnection by lazy { CameraVideoConnection(selector, address, port) }

    public override val connectionObservable: Flow<CameraConnectionState>
        get() = super.connectionObservable.map {
            if (it is CameraConnectionState.AbstractConnected) {
                CameraConnectionState.Connected(setupMovementEvent(it))
            } else it
        }
            .shareIn(
                GlobalScope + CoroutineName("camera_connection_${address}_$port"),
                SharingStarted.WhileSubscribed(replayExpirationMillis = 0),
                1
            )

    val videoObservable: Flow<ByteArray> = cameraVideoConnection.observeVideoStream

    private fun setupMovementEvent(
        state: CameraConnectionState.AbstractConnected,
    ) = state.receive
        .filter { it.messageId == CommandCode.ALARM_REQ }
        .map { }
        .onStart {
            log.debug("Requesting movement event from $address:$port")
            state.send(CommandRepository.alarmStart(state.sessionId))
        }
        .shareIn(
            state.connectionScope +
                    CoroutineName("camera_connection_${address}_${port}_movement_event"),
            SharingStarted.Lazily
        )
}