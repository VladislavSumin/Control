package ru.falseteam.control.server.api

import io.ktor.routing.*

interface Api {
    fun install(routing: Routing): Routing
}