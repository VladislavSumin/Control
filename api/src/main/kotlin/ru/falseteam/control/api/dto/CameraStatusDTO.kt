package ru.falseteam.control.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class CameraStatusDTO {
    abstract val id: Long

    @Serializable
    @SerialName("connecting")
    data class Connecting(override val id: Long) : CameraStatusDTO()

    @Serializable
    @SerialName("connected")
    data class Connected(override val id: Long) : CameraStatusDTO()

    @Serializable
    @SerialName("disconnected")
    data class Disconnected(override val id: Long) : CameraStatusDTO()
}