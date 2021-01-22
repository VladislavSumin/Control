package ru.falseteam.control.domain.servers

import java.net.URI

interface ServersInteractor {
    suspend fun getPrimaryServer(): ServerInfo
    suspend fun setPrimaryServerUrl(url: URI)
}