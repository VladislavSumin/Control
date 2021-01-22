package ru.falseteam.control.api

import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.control.domain.servers.ServerInfo

class CamsApiImpl(private val httpClient: HttpClient) : CamsApi {
    override suspend fun getAll(server: ServerInfo): List<CameraDTO> =
        httpClient.get(server, "/api/v1/cams")

    override suspend fun addCamera(server: ServerInfo, cameraDTO: CameraDTO) {
        httpClient.put<Unit>("${server.url}/api/v1/cams") {
            contentType(ContentType.Application.Json)
            body = cameraDTO
        }
    }

    override fun getVideoStream(server: ServerInfo, id: Long): Flow<ByteArray> = flow {
        httpClient.webSocket(
            host = "${server.hostname}:${server.port}",
            path = "/api/v1/cams/stream/$id"
        ) {
            for (frame in incoming) {
                emit((frame as Frame.Binary).data)
            }
        }
    }
}