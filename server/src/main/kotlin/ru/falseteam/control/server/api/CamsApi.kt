package ru.falseteam.control.server.api

import io.ktor.request.*
import io.ktor.routing.*
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.control.server.domain.cams.CamsInteractor


interface CamsApi {
    fun install(routing: Routing): Routing
}