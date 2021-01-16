package ru.falseteam.control.domain.servers

private const val HTTP = "http://"
private const val HTTPS = "https://"

data class ServerInfo(
    val hostname: String,
    val port: Int,
    val useHttps: Boolean = false
) {
    private val schema = if (useHttps) HTTPS else HTTP
    val url = "$schema$hostname:$port"
}