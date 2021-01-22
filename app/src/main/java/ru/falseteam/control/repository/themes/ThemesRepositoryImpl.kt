package ru.falseteam.control.repository.themes

import android.content.Context
import com.russhwolf.settings.boolean
import ru.falseteam.control.repository.BaseRepository
import ru.falseteam.control.repository.Repository

class ThemesRepositoryImpl(context: Context) :
    BaseRepository(context, Repository.Themes), ThemesRepository {

    override var isDarkThemeEnabled: Boolean
            by settings.boolean("is_dark_theme_enabled", false)
}