package ru.falseteam.control.api.rsub

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.CameraStatusDTO
import ru.falseteam.rsub.RSubFlowPolicy
import ru.falseteam.rsub.RSubInterface

@RSubInterface("cams_status")
interface CamsStatusRSub {
    @RSubFlowPolicy(RSubFlowPolicy.Policy.SUPPRESS_EXCEPTION_AND_RECONNECT)
    fun observeAll(): Flow<Map<Long, CameraStatusDTO>>
}