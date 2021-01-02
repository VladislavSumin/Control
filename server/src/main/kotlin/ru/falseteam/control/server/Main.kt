package ru.falseteam.control.server

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*
import org.kodein.di.instance
import ru.falseteam.control.server.api.CamsApi
import ru.falseteam.control.server.di.Kodein
import ru.falseteam.rsub.connector.ktorwebsocket.server.rSubWebSocket
import ru.falseteam.rsub.server.RSubServer

fun main(args: Array<String>) {
    val rSubServer: RSubServer by Kodein.instance()
    val camsApi: CamsApi by Kodein.instance()

    embeddedServer(Netty, 8080) {
        install(ContentNegotiation) {
            json()
        }
        install(WebSockets)
        routing {
            camsApi.install(this)
            rSubWebSocket(rSubServer)
        }
    }.start(wait = true)
}