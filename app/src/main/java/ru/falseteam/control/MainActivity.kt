package ru.falseteam.control

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import ru.falseteam.control.ui.ControlTheme
import ru.falseteam.control.ui.screens.addcamera.AddCameraScreen
import ru.falseteam.control.ui.screens.cams.CamsScreen
import ru.falseteam.control.ui.screens.livestream.LivestreamScreen
import ru.falseteam.control.ui.screens.recors.RecordsScreen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ControlTheme {
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
        }
    }

    @Composable
    private fun MainContent(navController: NavHostController) {
        NavHost(navController = navController, startDestination = Screen.DefaultScreen.path) {
            composable(Screen.Cams.path) { CamsScreen(navController) }
            composable(Screen.Records.path) { RecordsScreen(navController) }
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
            BottomNavigation {
                navItems.forEach { screen ->
                    BottomNavigationItem(
                        icon = { /*Icon(Icons.Filled.Favorite)*/ },
                        label = { Text(screen.name) },
                        selected = currentRoute == screen.screen.path,
                        onClick = {
                            navController.navigate(screen.screen) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo = navController.graph.startDestination
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
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
    )

    private sealed class NavItem(val screen: Screen, val name: String) {
        object Records : NavItem(Screen.Records, "Records")
        object Cams : NavItem(Screen.Cams, "Cams")
    }
}