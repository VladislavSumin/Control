package ru.falseteam.control.server

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.kodein.di.instance
import ru.falseteam.control.server.database.Database
import ru.falseteam.control.server.di.Kodein
import ru.falseteam.rsub.connector.ktorwebsocket.server.rSubWebSocket
import ru.falseteam.rsub.server.RSubServer

fun main(args: Array<String>) {
    val rSubServer by Kodein.instance<RSubServer>()
    val test by Kodein.instance<Database>()
    runBlocking {
        test.cameraQueries.selectAll().asFlow().mapToList().collect {
            println(it)
        }
    }

    embeddedServer(Netty, 8080) {
        install(WebSockets)
        routing {
            rSubWebSocket(rSubServer)
        }
    }.start(wait = true)
}