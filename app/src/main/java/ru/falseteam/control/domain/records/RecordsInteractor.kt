package ru.falseteam.control.domain.records

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.CameraRecordDTO

interface RecordsInteractor {
    fun observeAll(): Flow<List<CameraRecordDTO>>
    suspend fun getAll(): List<CameraRecordDTO>
    suspend fun setKeepForever(id: Long, keepForever: Boolean)
}