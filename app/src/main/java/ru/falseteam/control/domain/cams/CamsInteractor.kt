package ru.falseteam.control.domain.cams

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.control.api.dto.CameraStatusDTO

interface CamsInteractor {
    fun observeAll(): Flow<List<CameraDTO>>
    fun observerStatus(): Flow<Map<Long, CameraStatusDTO>>
    suspend fun put(camera: CameraDTO)
    fun observeVideoStream(camera: CameraDTO): Flow<ByteArray>
}