package ru.falseteam.control.domain.entities

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.EntityInfoDTO
import ru.falseteam.control.api.rsub.EntitiesRSub

class EntitiesInteractorImpl(
    private val entitiesRSub: EntitiesRSub,
) : EntitiesInteractor {
    override fun observeAll(): Flow<Map<String, EntityInfoDTO>> = entitiesRSub.observeAll()
}