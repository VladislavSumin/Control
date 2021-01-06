package ru.falseteam.control.server.domain.records

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.control.api.dto.CameraRecordDto
import java.nio.file.Path
import java.sql.Timestamp

interface RecordsInteractor {
    fun observeAll(): Flow<List<CameraRecordDto>>

    suspend fun saveNewRecord(cameraDTO: CameraDTO, timestamp: Long, record: Path)
}