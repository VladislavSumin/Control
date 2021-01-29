package ru.falseteam.control.server.domain.configuration

import ru.falseteam.control.server.BuildConfig
import ru.falseteam.control.server.repository.ServerConfigurationRepository

class ConfigurationInteractorImpl(
    private val configurationRepository: ServerConfigurationRepository,
) : ConfigurationInteractor {
    override val isDebug: Boolean
        get() = configurationRepository.debug ?: BuildConfig.DEBUG
}