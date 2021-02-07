package ru.falseteam.control.api.dto

data class CameraRecordsInfoDTO(
    val totalCount: Int,
    val totalLength: Long,
    val totalSize: Long,
    val totalKeepForever: Int,
    val totalKeepForeverLength: Long,
    val totalKeepForeverSize: Long,
    val lastRecordTimestamp: Long,
)