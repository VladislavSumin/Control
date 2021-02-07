package ru.falseteam.control.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class CameraRecordsInfoDTO(
    val allRecords: RecordsCategoryInfo,
    val keepForeverRecords: RecordsCategoryInfo,
    val lastRecordTimestamp: Long,
) {
    @Serializable
    data class RecordsCategoryInfo(
        val totalCount: Int,
        val totalLength: Long,
        val totalSize: Long,
    )
}

