package ru.falseteam.control.server.rsub

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.CameraStatusDTO
import ru.falseteam.control.api.rsub.CamsStatusRSub
import ru.falseteam.control.server.domain.cams.CamsConnectionInteractor

class CamsStatusRSubImpl(
    private val camsConnectionInteractor: CamsConnectionInteractor,
) : CamsStatusRSub {
    override fun observeAll(): Flow<Map<Long, CameraStatusDTO>> = camsConnectionInteractor.observeStatus()
}