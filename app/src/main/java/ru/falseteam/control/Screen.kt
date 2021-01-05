package ru.falseteam.control

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.navigate

sealed class Screen(val path: String) {
    object Cams : Screen("cams")
    object AddCamera : Screen("add_camera")

    class Livestream(id: Long) : Screen("livestream/$id") {
        companion object {
            const val basePath = "livestream/{id}"
        }
    }
}

fun NavController.navigate(screen: Screen, builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(screen.path, builder)
}