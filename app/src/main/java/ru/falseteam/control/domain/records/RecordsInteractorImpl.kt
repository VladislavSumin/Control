package ru.falseteam.control.domain.records

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.RecordsApi
import ru.falseteam.control.api.dto.CameraRecordDTO
import ru.falseteam.control.api.rsub.CamsRecordRSub
import ru.falseteam.control.domain.servers.ServersInteractor

class RecordsInteractorImpl(
    private val camsRecordRSub: CamsRecordRSub,
    private val recordsApi: RecordsApi,
    private val serversInteractor: ServersInteractor,
) : RecordsInteractor {
    override fun observeAll(): Flow<List<CameraRecordDTO>> = camsRecordRSub.observeAll()
    override suspend fun getAll(): List<CameraRecordDTO> =
        recordsApi.getAll(serversInteractor.getPrimaryServer())
}