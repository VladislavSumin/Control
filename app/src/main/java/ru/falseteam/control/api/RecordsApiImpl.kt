package ru.falseteam.control.api

import io.ktor.client.*
import io.ktor.client.request.*
import ru.falseteam.control.api.dto.CameraRecordDTO
import ru.falseteam.control.domain.servers.ServerInfo

class RecordsApiImpl(
    private val httpClient: HttpClient
) : RecordsApi {
    override suspend fun getAll(serverInfo: ServerInfo): List<CameraRecordDTO> =
        httpClient.get(serverInfo, "/api/v1/records")

    override suspend fun getFiltered(
        serverInfo: ServerInfo,
        onlyKeepForever: Boolean,
        onlyNamed: Boolean
    ): List<CameraRecordDTO> = httpClient.get(serverInfo, "/api/v1/records") {
        parameter("only_keep_forever", onlyKeepForever)
        parameter("only_named", onlyNamed)
    }

    override suspend fun delete(serverInfo: ServerInfo, id: Long) =
        httpClient.delete<Unit>(serverInfo, "/api/v1/records/$id")

    override suspend fun setKeepForever(serverInfo: ServerInfo, id: Long, keepForever: Boolean) =
        httpClient.patch<Unit>(serverInfo, "/api/v1/records/keep_forever/$id") {
            parameter("value", keepForever)
        }
}