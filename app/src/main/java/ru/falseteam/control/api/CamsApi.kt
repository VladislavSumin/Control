package ru.falseteam.control.api

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.CameraDTO

interface CamsApi {
    suspend fun addCamera(cameraDTO: CameraDTO)
    fun getVideoStream(id:Long): Flow<ByteArray>
}