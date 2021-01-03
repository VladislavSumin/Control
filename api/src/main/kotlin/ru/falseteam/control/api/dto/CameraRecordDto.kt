package ru.falseteam.control.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class CameraRecordDto(
    val id: Long = 0L,
    val cameraId: Long,
//    val name: String,
//    val timestamp: Long = 0L,
//    val fileSize: Long = 0L,
//    val duration: Double? = null,
//    val keepForever: Boolean = false,
)