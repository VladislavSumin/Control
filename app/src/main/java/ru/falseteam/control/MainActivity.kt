package ru.falseteam.control

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import org.kodein.di.instance
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.control.di.Kodein
import ru.falseteam.control.domain.cams.CamsInteractor
import ru.falseteam.control.domain.cams.CamsInteractorImpl
import ru.falseteam.control.ui.ControlTheme
import ru.falseteam.control.ui.screens.AddCameraScreen
import ru.falseteam.control.ui.screens.CamsScreen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ControlTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Destinations.CamsScreen) {
                    composable(Destinations.CamsScreen) { CamsScreen(navController) }
                    composable(Destinations.AddCameraScreen) { AddCameraScreen(navController) }
                }
            }
        }
    }
}