package ru.falseteam.control.api.dto

data class CameraRecordsInfoDTO(
    val cameraId: Long,
    val totalCount: Int,
    val totalLength: Long,
    val totalSize: Long,
)