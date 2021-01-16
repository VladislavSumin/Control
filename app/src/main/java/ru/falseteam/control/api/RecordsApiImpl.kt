package ru.falseteam.control.api

import io.ktor.client.*
import io.ktor.client.request.*
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.control.api.dto.CameraRecordDTO
import ru.falseteam.control.domain.servers.ServerInfo

class RecordsApiImpl(
    private val httpClient: HttpClient
) : RecordsApi {
    override suspend fun getAll(serverInfo: ServerInfo): List<CameraRecordDTO> =
        httpClient.get(serverInfo, "/api/v1/records")
}