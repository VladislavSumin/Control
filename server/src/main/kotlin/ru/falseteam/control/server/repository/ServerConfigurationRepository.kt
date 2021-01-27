package ru.falseteam.control.server.repository

interface ServerConfigurationRepository {
    val recordsPath: String
    val recordsTmpPath: String
    val port: Int
}