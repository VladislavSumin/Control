package ru.falseteam.control.domain.cams

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.control.api.dto.CameraRecordsInfoDTO
import ru.falseteam.control.api.dto.CameraStatusDTO

interface CamsInteractor {
    fun observeAll(): Flow<List<CameraDTO>>
    suspend fun getAll(): List<CameraDTO>
    fun observeStatus(): Flow<Map<Long, CameraStatusDTO>>
    fun observeRecordsInfo(): Flow<Map<Long, CameraRecordsInfoDTO>>
    suspend fun put(camera: CameraDTO)
    suspend fun observeVideoStream(id: Long): Flow<ByteArray>
}