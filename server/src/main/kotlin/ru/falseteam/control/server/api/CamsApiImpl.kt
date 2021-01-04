package ru.falseteam.control.server.api

import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.collect
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.control.server.domain.cams.CamsConnectionInteractor
import ru.falseteam.control.server.domain.cams.CamsInteractor


class CamsApiImpl(
    private val camsInteractor: CamsInteractor,
    private val camsConnectionInteractor: CamsConnectionInteractor,
) : CamsApi {
    override fun install(routing: Routing) = routing.apply {
        put(path = "/api/v1/cams") {
            val camera = context.receive<CameraDTO>()
            camsInteractor.insert(camera)
            context.response.status(HttpStatusCode.OK)
        }

        webSocket(path = "/api/v1/livestream/{id}") {
            val id = call.parameters["id"]!!.toLong()
            camsConnectionInteractor.observeVideoStream(id).collect {
                this.send(Frame.Binary(false, it))
            }
        }
    }
}