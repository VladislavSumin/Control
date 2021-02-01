package ru.falseteam.control.api

import ru.falseteam.control.api.dto.CameraRecordDTO
import ru.falseteam.control.domain.servers.ServerInfo

interface RecordsApi {
    suspend fun getAll(serverInfo: ServerInfo): List<CameraRecordDTO>
    suspend fun getFiltered(
        serverInfo: ServerInfo,
        onlyKeepForever: Boolean = false,
        onlyNamed: Boolean = false,
        startTime: Long? = null,
        endTime: Long? = null,
        reverse: Boolean = false,
        cams: List<Long>? = null
    ): List<CameraRecordDTO>

    suspend fun delete(serverInfo: ServerInfo, id: Long)

    suspend fun setKeepForever(serverInfo: ServerInfo, id: Long, keepForever: Boolean)

    suspend fun rename(serverInfo: ServerInfo, id: Long, name: String)
}