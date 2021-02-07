package ru.falseteam.control.server.domain.entity

import ru.falseteam.control.api.dto.EntityInfoDTO

abstract class Entity(val id: String) {
    abstract val info: EntityInfoDTO
}