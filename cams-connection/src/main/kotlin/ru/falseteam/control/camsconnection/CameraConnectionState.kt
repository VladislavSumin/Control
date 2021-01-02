package ru.falseteam.control.camsconnection

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.camsconnection.protocol.Msg
import java.lang.Exception

sealed class CameraConnectionState {
    object Connecting : CameraConnectionState()
    data class Connected(
        val receive: Flow<Msg>,
        val send: suspend (msg: Msg) -> Unit
    ) : CameraConnectionState()

    data class Disconnected(val exception: Exception) : CameraConnectionState()
}