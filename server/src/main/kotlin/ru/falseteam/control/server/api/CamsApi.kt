package ru.falseteam.control.server.api

import io.ktor.routing.*

interface CamsApi {
    fun install(routing: Routing): Routing
}