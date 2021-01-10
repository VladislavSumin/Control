package ru.falseteam.control.domain.cams

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.CamsApi
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.control.api.dto.CameraStatusDTO
import ru.falseteam.control.api.rsub.CamsRSub
import ru.falseteam.control.api.rsub.CamsStatusRSub

class CamsInteractorImpl(
    private val camsRSub: CamsRSub,
    private val camsStatusRSub: CamsStatusRSub,
    private val camsApi: CamsApi,
) : CamsInteractor {
    override fun observeAll(): Flow<List<CameraDTO>> = camsRSub.observeAll()
    override fun observerStatus(): Flow<Map<Long, CameraStatusDTO>> = camsStatusRSub.observeAll()

    override suspend fun put(camera: CameraDTO) = camsApi.addCamera(camera)

    override fun observeVideoStream(id: Long): Flow<ByteArray> = camsApi.getVideoStream(id)
}