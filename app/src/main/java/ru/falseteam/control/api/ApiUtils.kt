package ru.falseteam.control.api

import io.ktor.client.*
import io.ktor.client.request.*
import ru.falseteam.control.domain.servers.ServerInfo

suspend inline fun <reified T> HttpClient.get(
    server: ServerInfo,
    path: String,
    block: HttpRequestBuilder.() -> Unit = {}
): T = get(
    scheme = server.schema,
    host = server.hostname,
    port = server.port,
    path = path,
    block = block
)

suspend inline fun <reified T> HttpClient.patch(
    server: ServerInfo,
    path: String,
    block: HttpRequestBuilder.() -> Unit = {}
): T = patch(
    scheme = server.schema,
    host = server.hostname,
    port = server.port,
    path = path,
    block = block
)

suspend inline fun <reified T> HttpClient.delete(
    server: ServerInfo,
    path: String,
    block: HttpRequestBuilder.() -> Unit = {}
): T = delete(
    scheme = server.schema,
    host = server.hostname,
    port = server.port,
    path = path,
    block = block
)