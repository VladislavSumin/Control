package ru.falseteam.control.domain.themes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import ru.falseteam.control.repository.themes.ThemesRepository

class ThemesInteractorImpl(
    private val themesRepository: ThemesRepository,
) : ThemesInteractor {
    private val isDarkThemeEnabledState = MutableStateFlow(themesRepository.isDarkThemeEnabled)

    override fun observeIsDarkTheme(): Flow<Boolean> = isDarkThemeEnabledState

    override suspend fun setIsDarkTheme(isEnabled: Boolean) {
        themesRepository.isDarkThemeEnabled = isEnabled
        isDarkThemeEnabledState.emit(isEnabled)
    }
}