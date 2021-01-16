package ru.falseteam.control.api

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.control.domain.servers.ServerInfo

interface CamsApi {
    suspend fun addCamera(server: ServerInfo, cameraDTO: CameraDTO)
    fun getVideoStream(id: Long): Flow<ByteArray>
}