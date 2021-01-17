package ru.falseteam.control.repository.themes

import android.content.Context
import com.russhwolf.settings.AndroidSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.boolean
import com.russhwolf.settings.string
import ru.falseteam.control.repository.RepositoryConstants

private const val PREFERENCE_NAME = RepositoryConstants.themes

private const val KEY_IS_DARK_THEME_ENABLED = "is_dark_theme_enabled"

class ThemesRepositoryImpl(context: Context) : ThemesRepository {
    private val settings: Settings = AndroidSettings(
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    )

    override var isDarkThemeEnabled: Boolean
            by settings.boolean(KEY_IS_DARK_THEME_ENABLED, false)

}