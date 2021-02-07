package ru.falseteam.control.server.domain.entity

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.EntityInfoDTO

interface EntityInteractor {
    suspend fun getEntitiesInfo(): Map<String, EntityInfoDTO>
    fun observeEntitiesInfo(): Flow<Map<String, EntityInfoDTO>>
}