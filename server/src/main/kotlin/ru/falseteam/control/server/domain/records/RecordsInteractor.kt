package ru.falseteam.control.server.domain.records

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.control.api.dto.CameraRecordDTO
import java.nio.file.Path

interface RecordsInteractor {
    fun observeAll(): Flow<List<CameraRecordDTO>>

    suspend fun saveNewRecord(cameraDTO: CameraDTO, timestamp: Long, record: Path)
}