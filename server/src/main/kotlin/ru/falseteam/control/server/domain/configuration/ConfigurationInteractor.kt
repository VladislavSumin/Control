package ru.falseteam.control.server.domain.configuration

interface ConfigurationInteractor {
    val isDebug: Boolean
    val isFullDebug: Boolean
}