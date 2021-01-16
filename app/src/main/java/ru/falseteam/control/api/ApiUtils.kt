package ru.falseteam.control.api

import io.ktor.client.*
import io.ktor.client.request.*
import ru.falseteam.control.domain.servers.ServerInfo

public suspend inline fun <reified T> HttpClient.get(
    server: ServerInfo,
    path: String,
): T = get(
    scheme = server.schema,
    host = server.hostname,
    port = server.port,
    path = path,
)