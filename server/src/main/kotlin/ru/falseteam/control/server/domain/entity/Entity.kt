package ru.falseteam.control.server.domain.entity

import kotlinx.coroutines.flow.MutableStateFlow
import ru.falseteam.control.api.dto.EntityInfoDTO
import ru.falseteam.control.api.dto.EntityStateDto

abstract class Entity(val id: String) {
    abstract val info: EntityInfoDTO
    val statesObservable = MutableStateFlow<Map<String, EntityStateDto>>(emptyMap())
}