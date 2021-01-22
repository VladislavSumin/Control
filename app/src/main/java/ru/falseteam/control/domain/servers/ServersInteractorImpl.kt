package ru.falseteam.control.domain.servers

import java.net.URI

class ServersInteractorImpl : ServersInteractor {
    override suspend fun getPrimaryServer(): ServerInfo = ServerInfo("10.0.0.56", 8080)

    override suspend fun setPrimaryServerUrl(url: URI) {
        println(url)
    }
}