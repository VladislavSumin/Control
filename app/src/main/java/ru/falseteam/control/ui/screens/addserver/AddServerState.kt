package ru.falseteam.control.ui.screens.addserver

sealed class AddServerState {
    object Input : AddServerState()
    object Success : AddServerState()
}