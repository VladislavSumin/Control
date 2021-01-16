package ru.falseteam.control.domain.servers

interface ServersInteractor {
    suspend fun getPrimaryServer(): ServerInfo
}