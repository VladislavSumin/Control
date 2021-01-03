package ru.falseteam.control.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class CameraStatusDTO {

    @Serializable
    @SerialName("connecting")
    object Connecting : CameraStatusDTO()

    @Serializable
    @SerialName("connected")
    object Connected : CameraStatusDTO()

    @Serializable
    @SerialName("disconnected")
    object Disconnected : CameraStatusDTO()
}