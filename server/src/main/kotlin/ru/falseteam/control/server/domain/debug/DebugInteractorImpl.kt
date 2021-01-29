package ru.falseteam.control.server.domain.debug

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.debug.CoroutineInfo
import kotlinx.coroutines.debug.DebugProbes
import kotlinx.coroutines.debug.State
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import ru.falseteam.control.server.domain.configuration.ConfigurationInteractor
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KProperty1
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.declaredMemberProperties
import kotlin.system.measureTimeMillis

class DebugInteractorImpl(
    configurationInteractor: ConfigurationInteractor,
) : DebugInteractor {
    private val log = LoggerFactory.getLogger("debugger")

    private val isDebug = configurationInteractor.isDebug
    private val isFullDebug = configurationInteractor.isFullDebug
    override fun configure() {
        log.info("Server debug: $isDebug")
        log.info("Server fullDebug: $isFullDebug")
        if (isDebug) {
            System.setProperty("kotlinx.coroutines.debug", "on")
            DebugProbes.install()
        }
    }

    override suspend fun run() = withContext(CoroutineName("debugger_dumper")) {
        delay(10_000)
        while (true) {
            val time = measureTimeMillis {
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
            }
            log.debug("Coroutine dumped at $time ms")
            delay(600_000)
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun CoroutineInfo.getString(): String {
        val id = getCoroutineId(context)
        val name = context[CoroutineName]?.name ?: "no_name"
        val dispatcher = context[CoroutineDispatcher].toString()

        val state = when (state) {
            State.CREATED -> "CREATED   "
            State.RUNNING -> "RUNNING   "
            State.SUSPENDED -> "SUSPENDED "
        }

        return StringBuilder()
            .append("--- ").append("%03d ".format(id))
            .append(state)
            .append(dispatcher.padEnd(25)).append(" ")
            .append(name)
            .let {
                if (isFullDebug) {
                    val stacktrace = this.lastObservedStackTrace()
                    it.append("\n\t\t\t").append(stacktrace.joinToString(separator = "\n\t\t\t"))
                } else it
            }
            .toString()
    }

    private fun getCoroutineId(context: CoroutineContext): Long {
        val coroutineId = context[coroutineIdKey]!!
        return coroutineIdProperty.get(coroutineId)
    }

    companion object {
        private val coroutineId = Class.forName("kotlinx.coroutines.CoroutineId").kotlin
        private val coroutineIdKey =
            coroutineId.companionObjectInstance!! as CoroutineContext.Key<*>

        @Suppress("UNCHECKED_CAST")
        private val coroutineIdProperty =
            coroutineId.declaredMemberProperties.first() as KProperty1<CoroutineContext.Element, Long>
    }
}