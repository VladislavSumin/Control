package ru.falseteam.control.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import kotlinx.coroutines.runBlocking
import org.kodein.di.direct
import org.kodein.di.instance
import ru.falseteam.control.di.Kodein
import ru.falseteam.control.domain.themes.ThemesInteractor
import ru.falseteam.uikit.elements.UikitSwitchListItem

@Composable
fun SettingsScreen() {
    Column {
        val themes = remember { Kodein.direct.instance<ThemesInteractor>() }
        val isDarkTheme by themes.observeIsDarkTheme().collectAsState(initial = null)
        if (isDarkTheme != null) {
            UikitSwitchListItem(
                text = "Темная тема",
                checked = isDarkTheme!!,
                onCheckedChange = { runBlocking { themes.setIsDarkTheme(it) } })
        }
    }
}