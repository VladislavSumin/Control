package ru.falseteam.control.server.domain.debug

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.debug.CoroutineInfo
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
        delay(10_000)
        while (true) {
            val coroutines = DebugProbes.dumpCoroutinesInfo().sortedBy { it.state }
            val suspended = coroutines.count { it.state == State.SUSPENDED }
            val running = coroutines.count { it.state == State.RUNNING }
            log.debug(
                "Dumped coroutines total=${coroutines.count()}, suspended=$suspended, running=$running"
            )
            val dump = coroutines.joinToString(
                separator =
                "\n\t", prefix = "Coroutines dump: \n\t"
            ) { it.getString() }
            log.trace(dump)
            delay(600_000)
        }
    }

    private fun CoroutineInfo.getString(): String {
        val state = when (state) {
            State.CREATED -> "CREATED  "
            State.RUNNING -> "RUNNING  "
            State.SUSPENDED -> "SUSPENDED"
        }
        return "--- $state $context"
    }
}