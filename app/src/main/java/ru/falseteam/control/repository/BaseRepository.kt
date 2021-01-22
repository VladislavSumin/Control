package ru.falseteam.control.repository

import android.content.Context
import com.russhwolf.settings.AndroidSettings
import com.russhwolf.settings.Settings

abstract class BaseRepository(context: Context, repository: Repository) {
    protected val settings: Settings = AndroidSettings(
        context.getSharedPreferences(repository.rawName, Context.MODE_PRIVATE)
    )
}