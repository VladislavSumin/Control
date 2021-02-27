package ru.falseteam.control.ui.screens.main

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import ru.falseteam.control.R
import ru.falseteam.control.ui.screens.Screen
import ru.falseteam.control.ui.screens.addcamera.AddCameraScreen
import ru.falseteam.control.ui.screens.addserver.AddServerScreen
import ru.falseteam.control.ui.screens.cams.CamsScreen
import ru.falseteam.control.ui.screens.livestream.LivestreamScreen
import ru.falseteam.control.ui.screens.navigate
import ru.falseteam.control.ui.screens.popUpTo
import ru.falseteam.control.ui.screens.recors.RecordsScreen
import ru.falseteam.control.ui.screens.settings.SettingsScreen

val LocalNavigation = staticCompositionLocalOf<NavController> {
    throw RuntimeException()
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    CompositionLocalProvider(
        LocalNavigation provides navController
    ) {
        Scaffold(
            bottomBar = { Navigation(navController = navController, navItems = navItems) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(it)
            ) {
                MainContent(navController = navController)
            }
        }
    }
}

@Composable
private fun MainContent(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.StartScreen.path) {
        composable(Screen.Cams.path) { CamsScreen() }
        composable(Screen.Records.path) { RecordsScreen() }
        composable(Screen.Settings.path) { SettingsScreen() }
        composable(Screen.AddCamera.path) { AddCameraScreen() }
        composable(Screen.AddServer.path) { AddServerScreen() }
        composable(
            Screen.Livestream.basePath,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { LivestreamScreen(it.arguments!!.getLong("id")) }
    }
}

@Composable
private fun Navigation(navController: NavController, navItems: List<NavItem>) {
    // This state needed to refresh navigation when changed
    navController.currentBackStackEntryAsState().value
    // But in part of case this return null
    // navBackStackEntry?.arguments?.getString(KEY_ROUTE)
    // And at same time this work normally
    // TODO check situation after library update
    val currentRoute =
        navController.currentDestination?.arguments?.get(KEY_ROUTE)?.defaultValue
            ?: Screen.StartScreen.path
    if (navItems.find { it.screen.path == currentRoute } != null) {
        BottomNavigation(backgroundColor = MaterialTheme.colors.surface, elevation = 12.dp) {
            navItems.forEach { navItem ->
                BottomNavigationItem(
                    icon = { Icon(ImageVector.vectorResource(id = navItem.icon), null) },
                    label = { Text(navItem.name) },
                    selected = currentRoute == navItem.screen.path,
                    onClick = {
                        if (navItem.screen.path != currentRoute) {
                            navController.navigate(navItem.screen) {
                                popUpTo(Screen.HomeScreen)
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }
    }
}

private val navItems = listOf(
    NavItem.Records,
    NavItem.Cams,
    NavItem.Settings,
)

private sealed class NavItem(
    val screen: Screen,
    val name: String,
    @DrawableRes val icon: Int,
) {
    object Records : NavItem(Screen.Records, "Records", R.drawable.ic_camera_records)
    object Cams : NavItem(Screen.Cams, "Cams", R.drawable.ic_security_camera)
    object Settings : NavItem(Screen.Settings, "Settings", R.drawable.ic_settings)
}