package ru.falseteam.control.server.api

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.collect
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.control.server.domain.cams.CamsConnectionInteractor
import ru.falseteam.control.server.domain.cams.CamsInteractor

class CamsApi(
    private val camsInteractor: CamsInteractor,
    private val camsConnectionInteractor: CamsConnectionInteractor,
) : Api {
    override fun install(routing: Routing) = routing.apply {
        get(path = "/api/v1/cams") {
            call.respond(camsInteractor.getAll())
        }

        put(path = "/api/v1/cams") {
            val camera = context.receive<CameraDTO>()
            camsInteractor.insert(camera)
            context.response.status(HttpStatusCode.OK)
        }

        get(path = "/api/v1/cams/{id}") {
            val id = call.parameters["id"]!!.toLong()
            val record = camsInteractor.getById(id)
            if (record != null) call.respond(record)
            else call.respond(HttpStatusCode.NotFound)
        }

        webSocket(path = "/api/v1/cams/stream/{id}") {
            val id = call.parameters["id"]!!.toLong()
            camsConnectionInteractor.observeVideoStream(id).collect {
                this.send(Frame.Binary(true, it))
            }
        }
    }
}