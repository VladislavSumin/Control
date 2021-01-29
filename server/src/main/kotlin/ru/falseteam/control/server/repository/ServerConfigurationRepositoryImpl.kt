package ru.falseteam.control.server.repository

import com.russhwolf.settings.*
import java.io.File
import java.io.FileInputStream
import java.util.*

class ServerConfigurationRepositoryImpl : ServerConfigurationRepository {
    private val properties = Properties().apply {
        val file = File("server.properties")
        file.createNewFile()
        load(FileInputStream(file))
    }
    private val settings: Settings = JvmPropertiesSettings(properties)

    override val recordsPath: String by settings.string("recordsPath", "data/records")
    override val recordsTmpPath: String by settings.string("recordsTmpPath", "data/recordsTmp")
    override val port: Int by settings.int("port", 8080)
    override val debug: Boolean? by settings.nullableBoolean("debug")
    override val fullDebug: Boolean by settings.boolean("fullDebug", false)
}