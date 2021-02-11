package ru.falseteam.control.server.domain.entity

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import ru.falseteam.control.api.dto.EntityInfoDTO
import ru.falseteam.control.api.dto.EntityStateDto
import java.util.*

class EntityInteractorImpl : EntityInteractor {
    private val entitiesObservable = MutableStateFlow<Map<String, Entity>>(emptyMap())

    private val entitiesInfoObservable = entitiesObservable
        .map { it.mapValues { it.value.info } }
        .shareIn(GlobalScope, SharingStarted.Eagerly, 1)

    private val entitiesStatesObservable = entitiesObservable
        .flatMapLatest { entities ->
            val a = entities.map { (id, entity) -> entity.statesObservable.map { id to it } }
            combine(a) { it.toMap() }
        }

    init {
        val entities: MutableMap<String, Entity> = mutableMapOf()
        entities += SwitchEntity("test_btn/btn1")
        entities += SwitchEntity("test_btn/btn2")
        entities += SwitchEntity("test_btn/btn3")
        entities += SwitchEntity("test_btn/btn4")

        runBlocking {
            entitiesObservable.emit(entities.toMap())
        }
    }

    private suspend fun getEntities() = entitiesObservable.first()

    override suspend fun getEntitiesInfo(): Map<String, EntityInfoDTO> =
        entitiesInfoObservable.first()

    override fun observeEntitiesInfo(): Flow<Map<String, EntityInfoDTO>> = entitiesInfoObservable

    override suspend fun getEntitiesStates(): Map<String, Map<String, EntityStateDto>> =
        entitiesStatesObservable.first()

    override fun observeEntitiesStates(): Flow<Map<String, Map<String, EntityStateDto>>> =
        entitiesStatesObservable

    override suspend fun getEntity(id: String): Entity = getEntities().getValue(id)

    private operator fun MutableMap<String, Entity>.plusAssign(entity: Entity) {
        this[entity.id] = entity
    }
}