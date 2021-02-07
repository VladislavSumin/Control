package ru.falseteam.control.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class EntityInfoDTO(
    val type: String
)