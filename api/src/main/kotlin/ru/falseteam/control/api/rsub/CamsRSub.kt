package ru.falseteam.control.api.rsub

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.control.api.dto.CameraStatusDTO
import ru.falseteam.rsub.RSubFlowPolicy
import ru.falseteam.rsub.RSubInterface

@RSubInterface("cams")
interface CamsRSub {
    @RSubFlowPolicy(RSubFlowPolicy.Policy.SUPPRESS_EXCEPTION_AND_RECONNECT)
    fun observeAll(): Flow<List<CameraDTO>>

    @RSubFlowPolicy(RSubFlowPolicy.Policy.SUPPRESS_EXCEPTION_AND_RECONNECT)
    fun observeStatus(): Flow<Map<Long, CameraStatusDTO>>
}