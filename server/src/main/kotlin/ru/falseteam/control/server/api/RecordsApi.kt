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
            val file = recordsInteractor.getRecord(id).toFile()
            if (file.exists()) {
                call.respondFile(file)
            } else call.respond(HttpStatusCode.NotFound)
        }

        get("/api/v1/records/preview/{id}") {
            val id = call.parameters["id"]!!.toLong()
            val file = recordsInteractor.getPreview(id).toFile()
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
                val startTime = call.parameters["start_time"]?.toLong()
                val endTime = call.parameters["end_time"]?.toLong()
                val reverse = call.parameters["reverse"]?.toBoolean() ?: false
                // TODO refactor
                val cams = call.parameters["cams"]
                    ?.trim { it == '[' || it == ']' }
                    ?.split(',')
                    ?.map { it.trim().toLong() }
                call.respond(
                    recordsInteractor.getFiltered(
                        onlyKeepForever, onlyNamed, startTime, endTime, reverse, cams
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

        patch("/api/v1/records/rename/{id}") {
            val id = call.parameters["id"]!!.toLong()
            val name = call.parameters["name"]
            recordsInteractor.rename(id, name)
            call.respond(HttpStatusCode.OK)
        }

        get("/api/v1/records/{id}") {
            val id = call.parameters["id"]!!.toLong()
            val record = recordsInteractor.getById(id)
            if (record != null) call.respond(record)
            else call.respond(HttpStatusCode.NotFound)
        }

        delete("/api/v1/records/{id}") {
            val id = call.parameters["id"]!!.toLong()
            recordsInteractor.delete(id)
            call.respond(HttpStatusCode.OK)
        }
    }
}