package ru.falseteam.control.server.api.entity

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import ru.falseteam.control.server.api.Api
import ru.falseteam.control.server.domain.entity.EntityInteractor
import ru.falseteam.control.server.domain.entity.SwitchEntity

class SwitchEntityApi(
    private val entityInteractor: EntityInteractor
) : Api {
    override fun install(routing: Routing): Routing = routing.apply {
        post(path = "/api/v1/entities/switch/set_state") {
            val id = call.parameters["id"]!!
            val state = call.parameters["state"]!!.toBoolean()
            (entityInteractor.getEntity(id) as SwitchEntity).setState(state)
            call.respond(HttpStatusCode.OK)
        }
    }
}