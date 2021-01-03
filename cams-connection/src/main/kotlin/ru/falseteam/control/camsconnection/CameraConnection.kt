package ru.falseteam.control.camsconnection

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import ru.falseteam.control.camsconnection.protocol.CommandCode
import ru.falseteam.control.camsconnection.protocol.CommandRepository

class CameraConnection(private val address: String, private val port: Int) :
    AbstractCameraConnection(address, port) {
    public override val connectionObservable: Flow<CameraConnectionState>
        get() = super.connectionObservable.map {
            if (it is CameraConnectionState.AbstractConnected) {
                CameraConnectionState.Connected(
                    setupMovementEvent(it),
                    setupVideoFlow()
                )
            } else it
        }
            .flowOn(Dispatchers.IO)
            .shareIn(GlobalScope, SharingStarted.WhileSubscribed(replayExpirationMillis = 0), 1)

    private fun setupVideoFlow(): Flow<ByteArray> =
        CameraVideoConnection(address, port).connectionObservable
            .filter {
                when (it) {
                    is CameraConnectionState.Connecting -> false
                    is CameraConnectionState.ConnectedVideo -> true
                    is CameraConnectionState.Disconnected -> throw it.exception
                    else -> throw Exception("Unexpected state: $it")
                }
            }
            .map { it as CameraConnectionState.ConnectedVideo }
            .flatMapLatest { it.videoFlow }

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