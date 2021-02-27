package ru.falseteam.control.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import org.kodein.di.direct
import org.kodein.di.instance
import ru.falseteam.control.di.Kodein
import ru.falseteam.control.domain.themes.ThemesInteractor
import ru.falseteam.control.ui.screens.main.MainScreen
import ru.falseteam.uikit.ControlTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkTheme by
            Kodein.direct.instance<ThemesInteractor>().observeIsDarkTheme().collectAsState(
                initial = false
            )
            LocalContext
            ControlTheme(darkTheme = isDarkTheme) {
                MainScreen()
            }
        }
    }
}