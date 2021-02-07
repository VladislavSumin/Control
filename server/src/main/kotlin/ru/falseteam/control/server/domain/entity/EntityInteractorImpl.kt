package ru.falseteam.control.server.domain.entity

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import ru.falseteam.control.api.dto.EntityInfoDTO
import java.util.*

class EntityInteractorImpl : EntityInteractor {
    private val entities: MutableMap<String, Entity> = Collections.synchronizedMap(
        mutableMapOf()
    )

    private val entitiesObservable = MutableStateFlow(entities.toMap())

    private val entitiesInfoObservable = entitiesObservable
        .map { it.mapValues { it.value.info } }
        .shareIn(GlobalScope, SharingStarted.Eagerly, 1)

    init {
        entities += SwitchEntity("test_btn/btn1")
        entities += SwitchEntity("test_btn/btn2")
        entities += SwitchEntity("test_btn/btn3")
        entities += SwitchEntity("test_btn/btn4")
        runBlocking {
            entitiesObservable.emit(entities.toMap())
        }
    }

    override suspend fun getEntitiesInfo(): Map<String, EntityInfoDTO> =
        entitiesInfoObservable.first()

    override fun observeEntitiesInfo(): Flow<Map<String, EntityInfoDTO>> = entitiesInfoObservable

    private operator fun MutableMap<String, Entity>.plusAssign(entity: Entity) {
        this[entity.id] = entity
    }
}