package ru.falseteam.control.server.repository

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.CameraRecordDTO

interface CameraRecordsRepository {

    suspend fun getAll(): List<CameraRecordDTO>
    fun observeAll(): Flow<List<CameraRecordDTO>>

    suspend fun getOrNull(id: Long): CameraRecordDTO?
    suspend fun get(id: Long): CameraRecordDTO

    suspend fun insert(record: CameraRecordDTO): Long

    suspend fun setKeepForever(id: Long, keepForever: Boolean)
    suspend fun rename(id: Long, name: String?)


    suspend fun delete(id: Long)
}