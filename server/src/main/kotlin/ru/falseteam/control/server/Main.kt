package ru.falseteam.control.server

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import org.apache.logging.log4j.LogManager
import org.kodein.di.instance
import org.slf4j.LoggerFactory
import ru.falseteam.control.server.api.CamsApi
import ru.falseteam.control.server.di.Kodein
import ru.falseteam.control.server.domain.cams.CamsConnectionInteractor
import ru.falseteam.rsub.connector.ktorwebsocket.server.rSubWebSocket
import ru.falseteam.rsub.server.RSubServer

fun main(args: Array<String>) {
    val log = LoggerFactory.getLogger("control.main")
    log.info("Starting server")

    val rSubServer: RSubServer by Kodein.instance()
    val camsApi: CamsApi by Kodein.instance()
    val camsConnectionInteractor: CamsConnectionInteractor by Kodein.instance()

    val server = embeddedServer(Netty, 8080) {
        install(ContentNegotiation) {
            json()
        }
        install(WebSockets)
        routing {
            camsApi.install(this)
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
