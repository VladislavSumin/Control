package ru.falseteam.control.server

import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*
import org.kodein.di.instance
import ru.falseteam.control.server.di.Kodein
import ru.falseteam.rsub.connector.ktorwebsocket.server.rSubWebSocket
import ru.falseteam.rsub.server.RSubServer

fun main(args: Array<String>) {
    val rSubServer by Kodein.instance<RSubServer>()

    embeddedServer(Netty, 8080) {
        install(WebSockets)
        routing {
            rSubWebSocket(rSubServer)
        }
    }.start(wait = true)
}