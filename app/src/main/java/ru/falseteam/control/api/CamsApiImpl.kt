package ru.falseteam.control.api

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import ru.falseteam.control.api.dto.CameraDTO

private const val ADDRESS = "10.0.0.56"

class CamsApiImpl(private val httpClient: HttpClient) : CamsApi {
    override suspend fun put(cameraDTO: CameraDTO) {
        httpClient.put<Unit>(ADDRESS) {
            contentType(ContentType.Application.Json)
            body = cameraDTO
        }
    }
}