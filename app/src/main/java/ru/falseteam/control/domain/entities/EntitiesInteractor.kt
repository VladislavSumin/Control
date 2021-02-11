package ru.falseteam.control.domain.entities

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.EntityInfoDTO
import ru.falseteam.control.api.dto.EntityStateDto

interface EntitiesInteractor {
    fun observeAll(): Flow<Map<String, EntityInfoDTO>>
    fun observeStates(): Flow<Map<String, Map<String, EntityStateDto>>>
}