package ru.falseteam.control.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class CameraDTO(
    val id: Int,
    val name: String,
    val address: String,
)