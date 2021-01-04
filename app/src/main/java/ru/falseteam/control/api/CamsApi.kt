package ru.falseteam.control.api

import io.ktor.client.*
import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.CameraDTO

interface CamsApi {
    suspend fun put(cameraDTO: CameraDTO)
    fun getVideoStream(cameraDTO: CameraDTO): Flow<ByteArray>
}