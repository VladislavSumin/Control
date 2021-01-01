package ru.falseteam.control.api

import io.ktor.client.*
import ru.falseteam.control.api.dto.CameraDTO

interface CamsApi {
    suspend fun put(cameraDTO: CameraDTO)
}