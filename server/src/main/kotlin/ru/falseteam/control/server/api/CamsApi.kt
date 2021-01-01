package ru.falseteam.control.server.api

import io.ktor.request.*
import io.ktor.routing.*
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.control.server.domain.cams.CamsInteractor


class CamsApi(
    private val camsInteractor: CamsInteractor
) {
    fun install(routing: Routing) = routing.apply {
        put(path = "/api/v1/cams") {
            val camera = context.receive<CameraDTO>()
            camsInteractor.insert(camera)
        }
    }
}