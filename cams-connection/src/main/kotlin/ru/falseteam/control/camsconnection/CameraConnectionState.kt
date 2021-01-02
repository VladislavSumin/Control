package ru.falseteam.control.camsconnection

import java.lang.Exception

sealed class CameraConnectionState {
    object Connecting : CameraConnectionState()
    object Connected : CameraConnectionState()
    data class Disconnected(val exception: Exception) : CameraConnectionState()
}