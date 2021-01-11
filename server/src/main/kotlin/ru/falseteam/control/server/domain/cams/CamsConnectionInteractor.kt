package ru.falseteam.control.server.domain.cams

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.CameraStatusDTO

interface CamsConnectionInteractor {
    suspend fun processConnections()

    fun observeStatus(): Flow<Map<Long, CameraStatusDTO>>
    suspend fun observeVideoStream(id: Long): Flow<ByteArray>
}