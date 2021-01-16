package ru.falseteam.control.api

import ru.falseteam.control.api.dto.CameraRecordDTO
import ru.falseteam.control.domain.servers.ServerInfo

interface RecordsApi {
    suspend fun getAll(serverInfo: ServerInfo): List<CameraRecordDTO>
    suspend fun setKeepForever(serverInfo: ServerInfo, id: Long, keepForever: Boolean)
}