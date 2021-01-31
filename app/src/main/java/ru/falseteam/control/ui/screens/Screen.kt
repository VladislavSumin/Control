package ru.falseteam.control.ui.screens

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.PopUpToBuilder
import androidx.navigation.compose.navigate
import androidx.navigation.compose.popUpTo

sealed class Screen(val path: String) {
    companion object {
        val StartScreen: Screen = AddServer
        val HomeScreen: Screen = Cams
    }

    object Cams : Screen("cams")
    object Records : Screen("records")
    object Settings : Screen("settings")
    object AddCamera : Screen("add_camera")
    object AddServer : Screen("add_server")

    class Livestream(id: Long) : Screen("livestream/$id") {
        companion object {
            const val basePath = "livestream/{id}"
        }
    }
}

fun NavController.navigate(screen: Screen, builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(screen.path, builder)
}

fun NavOptionsBuilder.popUpTo(screen: Screen, popUpToBuilder: PopUpToBuilder.() -> Unit = {}) {
    popUpTo(screen.path, popUpToBuilder)
}