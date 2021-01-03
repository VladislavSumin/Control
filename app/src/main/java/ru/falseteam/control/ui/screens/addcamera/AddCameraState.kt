package ru.falseteam.control.ui.screens.addcamera

sealed class AddCameraState {
    object Input : AddCameraState()
    object Loading : AddCameraState()
    object Success : AddCameraState()
}