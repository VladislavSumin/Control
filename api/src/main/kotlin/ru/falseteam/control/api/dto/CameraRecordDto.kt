package ru.falseteam.control.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class CameraRecordDto(
    val id: Long = 0L,
    val cameraId: Long,
    val name: String?,
    val timestamp: Long,
    val fileSize: Long,
    val length: Long
)