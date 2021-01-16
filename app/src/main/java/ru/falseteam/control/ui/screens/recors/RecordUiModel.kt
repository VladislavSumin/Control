package ru.falseteam.control.ui.screens.recors

data class RecordUiModel(
    val id: Long,
    val name: String?,
    val cameraName: String?,
    val date: String,
    val keepForever: Boolean,
)