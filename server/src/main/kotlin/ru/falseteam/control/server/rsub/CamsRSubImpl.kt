package ru.falseteam.control.server.rsub

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.control.api.rsub.CamsRSub
import ru.falseteam.control.server.domain.cams.CamsInteractor

class CamsRSubImpl(
    private val camsInteractor: CamsInteractor,
) : CamsRSub {
    override fun observeAll(): Flow<List<CameraDTO>> = camsInteractor.observeAll()
}