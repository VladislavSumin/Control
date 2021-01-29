package ru.falseteam.control.server.domain.debug

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.debug.DebugProbes
import kotlinx.coroutines.debug.State
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
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

    override suspend fun run() = withContext(CoroutineName("debugger_dumper")) {
        while (true) {
            delay(10_000)
            val coroutines = DebugProbes.dumpCoroutinesInfo()
            val suspended = coroutines.count { it.state == State.SUSPENDED }
            val running = coroutines.count { it.state == State.RUNNING }
            log.debug(
                "Dumped coroutines total=${coroutines.count()}, suspended=$suspended, running=$running"
            )
            val dump = coroutines.joinToString(
                separator =
                "\n\t", prefix = "Coroutines dump: \n\t"
            )
            log.trace(dump)
        }
    }
}