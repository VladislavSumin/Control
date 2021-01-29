package ru.falseteam.control.server.domain.debug

import kotlinx.coroutines.debug.DebugProbes
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import ru.falseteam.control.server.domain.configuration.ConfigurationInteractor

class DebugInteractorImpl(
    configurationInteractor: ConfigurationInteractor,
) : DebugInteractor {
    private val log = LoggerFactory.getLogger("debugger")

    private val isDebug = configurationInteractor.isDebug
    override fun configure() {
        log.info("Server debug: $isDebug")
        if (isDebug) {
            System.setProperty("kotlinx.coroutines.debug", "on")
            DebugProbes.install()
        }
    }

    override suspend fun run() {
        while (true) {
            delay(10_000)
            val coroutines = DebugProbes.dumpCoroutinesInfo()
            coroutines.forEach {
                println(it)
            }
        }
    }
}