package ru.falseteam.control.server.api

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import ru.falseteam.control.server.domain.entity.EntityInteractor

class EntityApi(
    private val entityInteractor: EntityInteractor
) : Api {
    override fun install(routing: Routing): Routing = routing.apply {
        get(path = "/api/v1/entities") {
            call.respond(entityInteractor.getEntitiesInfo())
        }

        get(path = "/api/v1/entities/states") {
            call.respond(entityInteractor.getEntitiesStates())
        }
    }
}