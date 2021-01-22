package ru.falseteam.control.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.runBlocking
import org.kodein.di.direct
import org.kodein.di.instance
import ru.falseteam.control.di.Kodein
import ru.falseteam.control.domain.themes.ThemesInteractor

@Composable
fun SettingsScreen() {
    Column(modifier = Modifier.padding(16.dp)) {
        Row {
            Text(
                modifier = Modifier.weight(1f),
                text = "Темная тема"
            )
            // TODO add ViewModel
            val themes = remember { Kodein.direct.instance<ThemesInteractor>() }
            val isDarkTheme by themes.observeIsDarkTheme().collectAsState(initial = null)
            if (isDarkTheme != null) {
                Switch(
                    checked = isDarkTheme!!,
                    onCheckedChange = { runBlocking { themes.setIsDarkTheme(it) } })
            }
        }
    }
}