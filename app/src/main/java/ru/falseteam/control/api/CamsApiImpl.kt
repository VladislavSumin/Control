package ru.falseteam.control.api

import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.falseteam.control.api.dto.CameraDTO

private const val HOSTNAME = "10.0.0.56:8080"
private const val ADDRESS = "http://$HOSTNAME"

class CamsApiImpl(private val httpClient: HttpClient) : CamsApi {
    override suspend fun put(cameraDTO: CameraDTO) {
        httpClient.put<Unit>("$ADDRESS/api/v1/cams") {
            contentType(ContentType.Application.Json)
            body = cameraDTO
        }
    }

    override fun getVideoStream(id: Long): Flow<ByteArray> = flow {
        httpClient.webSocket(host = HOSTNAME, path = "/api/v1/livestream/$id") {
            for (frame in incoming) {
                emit((frame as Frame.Binary).data)
            }
        }
    }
}