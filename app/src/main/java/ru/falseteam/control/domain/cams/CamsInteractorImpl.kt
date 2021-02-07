package ru.falseteam.control.domain.cams

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.CamsApi
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.control.api.dto.CameraRecordsInfoDTO
import ru.falseteam.control.api.dto.CameraStatusDTO
import ru.falseteam.control.api.rsub.CamsRSub
import ru.falseteam.control.domain.servers.ServersInteractor

class CamsInteractorImpl(
    private val camsRSub: CamsRSub,
    private val camsApi: CamsApi,
    private val serversInteractor: ServersInteractor,
) : CamsInteractor {
    override fun observeAll(): Flow<List<CameraDTO>> = camsRSub.observeAll()
    override suspend fun getAll(): List<CameraDTO> =
        camsApi.getAll(serversInteractor.getPrimaryServer())

    override fun observeStatus(): Flow<Map<Long, CameraStatusDTO>> = camsRSub.observeStatus()
    override fun observeRecordsInfo(): Flow<Map<Long, CameraRecordsInfoDTO>> =
        camsRSub.observeRecordsInfo()

    override suspend fun put(camera: CameraDTO) =
        camsApi.addCamera(serversInteractor.getPrimaryServer(), camera)

    override suspend fun observeVideoStream(id: Long): Flow<ByteArray> =
        camsApi.getVideoStream(serversInteractor.getPrimaryServer(), id)
}