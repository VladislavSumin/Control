package ru.falseteam.control.server.domain.records

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.control.api.dto.CameraRecordDTO
import java.io.File
import java.nio.file.Path

interface RecordsInteractor {
    fun observeAll(): Flow<List<CameraRecordDTO>>
    suspend fun getAll(): List<CameraRecordDTO>
    suspend fun getById(id: Long): CameraRecordDTO?

    suspend fun saveNewRecord(cameraDTO: CameraDTO, timestamp: Long, record: Path)
    fun getRecord(id: Long): File
}