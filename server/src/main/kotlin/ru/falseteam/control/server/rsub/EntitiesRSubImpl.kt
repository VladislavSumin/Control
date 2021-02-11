package ru.falseteam.control.server.rsub

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.EntityInfoDTO
import ru.falseteam.control.api.dto.EntityStateDto
import ru.falseteam.control.api.rsub.EntitiesRSub
import ru.falseteam.control.server.domain.entity.EntityInteractor

class EntitiesRSubImpl(
    private val entityInteractor: EntityInteractor
) : EntitiesRSub {
    override fun observeAll(): Flow<Map<String, EntityInfoDTO>> =
        entityInteractor.observeEntitiesInfo()

    override fun observeSates(): Flow<Map<String, Map<String, EntityStateDto>>> =
        entityInteractor.observeEntitiesStates()
}