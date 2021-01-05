package ru.falseteam.control.server.domain.records

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.CameraRecordDto

interface RecordsInteractor {
    fun observeAll(): Flow<List<CameraRecordDto>>
    suspend fun insert(cameraRecord: CameraRecordDto)
}