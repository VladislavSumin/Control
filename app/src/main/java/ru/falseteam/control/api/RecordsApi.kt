package ru.falseteam.control.api

import ru.falseteam.control.api.dto.CameraRecordDTO
import ru.falseteam.control.domain.servers.ServerInfo

interface RecordsApi {
    suspend fun getAll(serverInfo: ServerInfo): List<CameraRecordDTO>
    suspend fun getFiltered(
        serverInfo: ServerInfo,
        onlyKeepForever: Boolean = false,
        onlyNamed: Boolean = false,
    ): List<CameraRecordDTO>

    suspend fun delete(serverInfo: ServerInfo, id: Long)

    suspend fun setKeepForever(serverInfo: ServerInfo, id: Long, keepForever: Boolean)
}