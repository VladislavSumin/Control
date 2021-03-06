package ru.falseteam.control.server.domain.records

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.control.api.dto.CameraRecordDTO
import ru.falseteam.control.api.dto.CameraRecordsInfoDTO
import java.io.File
import java.nio.file.Path

interface RecordsInteractor {
    suspend fun getAll(): List<CameraRecordDTO>
    fun observeAll(): Flow<List<CameraRecordDTO>>

    suspend fun getFiltered(
        onlyKeepForever: Boolean = false,
        onlyNamed: Boolean = false,
        startTime: Long? = null,
        endTime: Long? = null,
        reverse: Boolean = false,
        cams: List<Long>? = null
    ): List<CameraRecordDTO>

    suspend fun delete(id: Long)

    suspend fun getById(id: Long): CameraRecordDTO?

    suspend fun setKeepForever(id: Long, keepForever: Boolean)

    suspend fun rename(id: Long, name: String?)

    suspend fun saveNewRecord(cameraDTO: CameraDTO, timestamp: Long, record: Path)

    fun getRecordsTmpPath(): Path

    fun getRecord(id: Long): Path

    fun getPreview(id: Long): Path

    fun observeRecordsInfo(): Flow<Map<Long, CameraRecordsInfoDTO>>
}