package ru.falseteam.control.domain.records

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.CameraRecordDTO

interface RecordsInteractor {
    fun observeAll(): Flow<List<CameraRecordDTO>>
    suspend fun getAll(): List<CameraRecordDTO>
    suspend fun getFiltered(
        onlyKeepForever: Boolean = false,
        onlyNamed: Boolean = false,
        startTime: Long? = null,
        endTime: Long? = null,
        reverse: Boolean = false,
        cams: List<Long>? = null
    ): List<CameraRecordDTO>

    suspend fun delete(id: Long)

    suspend fun setKeepForever(id: Long, keepForever: Boolean)

    suspend fun rename(id: Long, name: String)

    suspend fun getRecordUri(id: Long): Uri
}