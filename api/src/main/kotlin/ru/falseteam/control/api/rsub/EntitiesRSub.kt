package ru.falseteam.control.api.rsub

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.EntityInfoDTO
import ru.falseteam.control.api.dto.EntityStateDto
import ru.falseteam.rsub.RSubFlowPolicy
import ru.falseteam.rsub.RSubInterface

@RSubInterface("entities")
interface EntitiesRSub {
    @RSubFlowPolicy(RSubFlowPolicy.Policy.SUPPRESS_EXCEPTION_AND_RECONNECT)
    fun observeAll(): Flow<Map<String, EntityInfoDTO>>

    @RSubFlowPolicy(RSubFlowPolicy.Policy.SUPPRESS_EXCEPTION_AND_RECONNECT)
    fun observeSates(): Flow<Map<String, Map<String, EntityStateDto>>>
}