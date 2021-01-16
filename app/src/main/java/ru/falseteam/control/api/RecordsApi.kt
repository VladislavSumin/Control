package ru.falseteam.control.api

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.control.api.dto.CameraRecordDTO
import ru.falseteam.control.domain.servers.ServerInfo

interface RecordsApi {
    suspend fun getAll(serverInfo: ServerInfo): List<CameraRecordDTO>
}