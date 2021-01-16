package ru.falseteam.control.domain.themes

import kotlinx.coroutines.flow.Flow

interface ThemesInteractor {
    fun observeIsDarkTheme(): Flow<Boolean>
    suspend fun setIsDarkTheme(isEnabled: Boolean)
}