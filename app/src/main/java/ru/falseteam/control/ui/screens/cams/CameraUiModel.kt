package ru.falseteam.control.ui.screens.cams

data class CameraUiModel(
    val id: Long,
    val name: String,
    val address: String,
    val isConnected: Boolean
)