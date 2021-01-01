package ru.falseteam.control.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class CameraDTO(
    val id: Long,
    val name: String,
    val address: String,
    val port: Int,
)