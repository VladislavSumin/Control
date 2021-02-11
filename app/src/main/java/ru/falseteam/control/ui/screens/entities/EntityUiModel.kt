package ru.falseteam.control.ui.screens.entities

import ru.falseteam.control.api.dto.EntityStateDto

data class EntityUiModel(
    val id: String,
    val type: String,
    val states: Map<String, EntityStateDto>
)