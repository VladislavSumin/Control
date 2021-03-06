package ru.falseteam.control.camsconnection

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.camsconnection.protocol.Msg
import java.lang.Exception

sealed class CameraConnectionState {
    object Connecting : CameraConnectionState()

    internal data class AbstractConnected(
        val connectionScope: CoroutineScope,
        val sessionId: Int,
        val receive: Flow<Msg>,
        val send: suspend (msg: Msg) -> Unit,
    ) : CameraConnectionState()

    data class Connected(
        val movementEvent: Flow<Unit>,
    ) : CameraConnectionState()

    data class Disconnected(val exception: Exception) : CameraConnectionState()
}