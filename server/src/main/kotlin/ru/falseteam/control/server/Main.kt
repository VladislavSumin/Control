package ru.falseteam.control.server

import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8888) {

    }.start(true)
}