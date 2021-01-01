package ru.falseteam.control.ui.screens

sealed class AddCameraState {
    object Input : AddCameraState()
    object Loading : AddCameraState()
//    object Success : AddCameraState()
}