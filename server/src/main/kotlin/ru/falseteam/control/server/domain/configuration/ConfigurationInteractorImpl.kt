package ru.falseteam.control.server.domain.configuration

import ru.falseteam.control.server.BuildConfig
import ru.falseteam.control.server.repository.ServerConfigurationRepository

class ConfigurationInteractorImpl(
    configurationRepository: ServerConfigurationRepository,
) : ConfigurationInteractor {
    override val isDebug: Boolean = configurationRepository.debug ?: BuildConfig.DEBUG
    override val isFullDebug: Boolean = configurationRepository.fullDebug
}