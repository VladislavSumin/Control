package ru.falseteam.control.server.domain.entity

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.EntityInfoDTO
import ru.falseteam.control.api.dto.EntityStateDto

interface EntityInteractor {
    suspend fun getEntitiesInfo(): Map<String, EntityInfoDTO>
    fun observeEntitiesInfo(): Flow<Map<String, EntityInfoDTO>>

    suspend fun getEntitiesStates(): Map<String, Map<String, EntityStateDto>>
    fun observeEntitiesStates(): Flow<Map<String, Map<String, EntityStateDto>>>
}