package ru.falseteam.control.domain.entities

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.EntityInfoDTO

interface EntitiesInteractor {
    fun observeAll(): Flow<Map<String, EntityInfoDTO>>
}