package ru.falseteam.control.server

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*
import io.sentry.Sentry
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import org.kodein.di.allInstances
import org.kodein.di.direct
import org.kodein.di.instance
import org.slf4j.LoggerFactory
import ru.falseteam.control.server.api.Api
import ru.falseteam.control.server.di.Kodein
import ru.falseteam.control.server.domain.cams.CamsConnectionInteractor
import ru.falseteam.control.server.domain.configuration.ConfigurationInteractor
import ru.falseteam.control.server.repository.ServerConfigurationRepository
import ru.falseteam.rsub.connector.ktorwebsocket.server.rSubWebSocket
import ru.falseteam.rsub.server.RSubServer

fun main(args: Array<String>) {
    val log = LoggerFactory.getLogger("control.main")
    log.info("Starting server")
    log.info("Server version: ${BuildConfig.version}")

    val configuration = Kodein.direct.instance<ConfigurationInteractor>()

    log.info("Server debug: ${configuration.isDebug}")

    if (!configuration.isDebug) {
        log.info("Start Sentry")
        Sentry.init {
            it.dsn = "https://0f84ed22e45a48dc984fe16c30eaa058@o512687.ingest.sentry.io/5613399"
        }
    }

    System.setProperty("kotlinx.coroutines.debug", if (configuration.isDebug) "on" else "off")

    val port = Kodein.direct.instance<ServerConfigurationRepository>().port
    val apis: List<Api> by Kodein.allInstances()
    val rSubServer: RSubServer by Kodein.instance()
    val camsConnectionInteractor: CamsConnectionInteractor by Kodein.instance()

    val server = embeddedServer(Netty, port) {
        install(ContentNegotiation) { json() }
        install(WebSockets)
        install(PartialContent)

        routing {
            apis.forEach { it.install(this) }
            rSubWebSocket(rSubServer)
        }
    }

    val rootJob = GlobalScope.launch {
        launch { camsConnectionInteractor.processConnections() }
    }

    server.addShutdownHook {
        runBlocking {
            log.info("Shutdown hook received")
            rootJob.cancelAndJoin()
            log.info("Server stopped")
            LogManager.shutdown()
        }
    }

    server.start(true)
}
