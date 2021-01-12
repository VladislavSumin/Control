package ru.falseteam.control.server.api

import io.ktor.routing.*

interface RecordsApi {
    fun install(routing: Routing): Routing
}