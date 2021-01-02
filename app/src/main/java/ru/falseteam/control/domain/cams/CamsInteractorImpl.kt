package ru.falseteam.control.domain.cams

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ru.falseteam.control.api.CamsApi
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.control.api.rsub.CamsRSub

class CamsInteractorImpl(
    private val camsRSub: CamsRSub,
    private val camsApi: CamsApi,
) : CamsInteractor {
    override fun observeAll(): Flow<List<CameraDTO>> = camsRSub.observeAll()
    override suspend fun put(camera: CameraDTO) = camsApi.put(camera)
}