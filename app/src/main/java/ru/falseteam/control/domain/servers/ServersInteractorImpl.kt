package ru.falseteam.control.domain.servers

import java.net.URL

class ServersInteractorImpl : ServersInteractor {
    override suspend fun getPrimaryServer(): ServerInfo = ServerInfo(URL("http://10.0.0.56:8080"))

    override suspend fun setPrimaryServerUrl(url: URL) {
        println(url)
    }
}