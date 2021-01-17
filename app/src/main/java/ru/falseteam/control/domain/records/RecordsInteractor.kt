package ru.falseteam.control.domain.records

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.CameraRecordDTO

interface RecordsInteractor {
    fun observeAll(): Flow<List<CameraRecordDTO>>
    suspend fun getAll(): List<CameraRecordDTO>
    suspend fun getFiltered(
        onlyKeepForever: Boolean = false,
        onlyNamed: Boolean = false,
    ): List<CameraRecordDTO>

    suspend fun delete(id:Long)

    suspend fun setKeepForever(id: Long, keepForever: Boolean)
}