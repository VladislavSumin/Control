package ru.falseteam.control.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.AmbientConfiguration
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import org.kodein.di.direct
import org.kodein.di.instance
import ru.falseteam.control.R
import ru.falseteam.control.di.Kodein
import ru.falseteam.control.domain.themes.ThemesInteractor
import ru.falseteam.control.ui.screens.Screen
import ru.falseteam.control.ui.screens.addcamera.AddCameraScreen
import ru.falseteam.control.ui.screens.cams.CamsScreen
import ru.falseteam.control.ui.screens.livestream.LivestreamScreen
import ru.falseteam.control.ui.screens.navigate
import ru.falseteam.control.ui.screens.recors.RecordsScreen
import ru.falseteam.control.ui.screens.settings.SettingsScreen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkTheme by
            Kodein.direct.instance<ThemesInteractor>().observeIsDarkTheme().collectAsState(
                initial = false
            )

            ControlTheme(darkTheme = isDarkTheme) {
                MainScreen()
            }
        }
    }

    @Composable
    private fun MainScreen() {
        val navController = rememberNavController()
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

    @Composable
    private fun MainContent(navController: NavHostController) {
        NavHost(navController = navController, startDestination = Screen.DefaultScreen.path) {
            composable(Screen.Cams.path) { CamsScreen(navController) }
            composable(Screen.Records.path) { RecordsScreen(navController) }
            composable(Screen.Settings.path) { SettingsScreen(navController) }
            composable(Screen.AddCamera.path) { AddCameraScreen(navController) }
            composable(
                Screen.Livestream.basePath,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { LivestreamScreen(it.arguments!!.getLong("id")) }
        }
    }

    @Composable
    private fun Navigation(navController: NavController, navItems: List<NavItem>) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute =
            navBackStackEntry?.arguments?.getString(KEY_ROUTE) ?: Screen.DefaultScreen.path
        if (navItems.find { it.screen.path == currentRoute } != null)
            BottomNavigation(backgroundColor = MaterialTheme.colors.surface, elevation = 12.dp) {
                navItems.forEach { navItem ->
                    BottomNavigationItem(
                        icon = { Icon(vectorResource(id = navItem.icon)) },
                        label = { Text(navItem.name) },
                        selected = currentRoute == navItem.screen.path,
                        onClick = {
                            navController.navigate(navItem.screen) {
                                popUpTo = navController.graph.startDestination
                                launchSingleTop = true
                            }
                        }
                    )
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
}