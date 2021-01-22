package ru.falseteam.control.domain.servers

import java.net.URL

private const val HTTP = "http"
private const val HTTPS = "https"

data class ServerInfo(
    val url: URL
) {
    val scheme = url.protocol!!
    val host = url.host!!
    val port = url.port
}