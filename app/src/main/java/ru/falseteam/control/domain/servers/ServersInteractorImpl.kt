package ru.falseteam.control.domain.servers

class ServersInteractorImpl : ServersInteractor {
    override suspend fun getPrimaryServer(): ServerInfo = ServerInfo("10.0.0.56", 8080)
}