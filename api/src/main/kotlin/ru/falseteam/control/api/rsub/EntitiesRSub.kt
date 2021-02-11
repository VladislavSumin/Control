package ru.falseteam.control.api.rsub

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.EntityInfoDTO
import ru.falseteam.rsub.RSubFlowPolicy
import ru.falseteam.rsub.RSubInterface

@RSubInterface("entities")
interface EntitiesRSub {
    @RSubFlowPolicy(RSubFlowPolicy.Policy.SUPPRESS_EXCEPTION_AND_RECONNECT)
    fun observeAll(): Flow<Map<String, EntityInfoDTO>>
}