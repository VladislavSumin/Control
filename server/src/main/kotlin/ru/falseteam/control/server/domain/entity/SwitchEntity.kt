package ru.falseteam.control.server.domain.entity

import kotlinx.coroutines.runBlocking
import ru.falseteam.control.api.dto.EntityInfoDTO
import ru.falseteam.control.api.dto.EntityStateDto

class SwitchEntity(id: String) : Entity(id) {
    companion object {
        val Info = EntityInfoDTO("switch")
    }

    init {
        runBlocking {
            statesObservable.emit(mapOf("isEnabled" to EntityStateDto.BooleanState(false)))
        }
    }

    override val info: EntityInfoDTO = Info

    suspend fun setState(state: Boolean) {
        statesObservable.emit(mapOf("isEnabled" to EntityStateDto.BooleanState(state)))
    }
}