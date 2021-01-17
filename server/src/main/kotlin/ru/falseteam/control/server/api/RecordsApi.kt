package ru.falseteam.control.server.api

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import ru.falseteam.control.server.domain.records.RecordsInteractor

class RecordsApi(
    private val recordsInteractor: RecordsInteractor,
) : Api {
    override fun install(routing: Routing) = routing.apply {
        get("/api/v1/records/video/{id}") {
            val id = call.parameters["id"]!!.toLong()
            val file = recordsInteractor.getRecord(id)
            if (file.exists()) {
                call.respondFile(file)
            } else call.respond(HttpStatusCode.NotFound)
        }

        get("/api/v1/records") {
            if (call.parameters.isEmpty()) {
                call.respond(recordsInteractor.getAll())
            } else {
                val onlyKeepForever = call.parameters["only_keep_forever"]?.toBoolean() ?: false
                val onlyNamed = call.parameters["only_named"]?.toBoolean() ?: false
                call.respond(
                    recordsInteractor.getFiltered(
                        onlyKeepForever, onlyNamed
                    )
                )
            }
        }

        patch("/api/v1/records/keep_forever/{id}") {
            val id = call.parameters["id"]!!.toLong()
            val value = call.parameters["value"].toBoolean()
            recordsInteractor.setKeepForever(id, value)
            call.respond(HttpStatusCode.OK)
        }

        get("/api/v1/records/{id}") {
            val id = call.parameters["id"]!!.toLong()
            val record = recordsInteractor.getById(id)
            if (record != null) call.respond(record)
            else call.respond(HttpStatusCode.NotFound)
        }
    }
}