package ru.falseteam.control.server.domain.entity

import ru.falseteam.control.api.dto.EntityInfoDTO

class SwitchEntity(id: String) : Entity(id) {
    companion object {
        val Info = EntityInfoDTO("switch")
    }

    override val info: EntityInfoDTO = Info
}