package ru.falseteam.control.domain.themes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class ThemesInteractorImpl : ThemesInteractor {
    private val isDarkThemeEnabledState = MutableStateFlow(false)

    override fun observeIsDarkTheme(): Flow<Boolean> = isDarkThemeEnabledState

    override suspend fun setIsDarkTheme(isEnabled: Boolean) =
        isDarkThemeEnabledState.emit(isEnabled)
}