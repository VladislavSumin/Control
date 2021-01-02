package ru.falseteam.control.server.api

import io.ktor.http.*
import io.ktor.http.cio.*
import io.ktor.request.*
import io.ktor.routing.*
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.control.server.domain.cams.CamsInteractor


class CamsApiImpl(
    private val camsInteractor: CamsInteractor
) : CamsApi {
    override fun install(routing: Routing) = routing.apply {
        put(path = "/api/v1/cams") {
            val camera = context.receive<CameraDTO>()
            camsInteractor.insert(camera)
            context.response.status(HttpStatusCode.OK)
        }
    }
}