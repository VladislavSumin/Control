package ru.falseteam.control.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class EntityStateDto {
    @Serializable
    @SerialName("boolean")
    data class BooleanState(val state: Boolean) : EntityStateDto()
}