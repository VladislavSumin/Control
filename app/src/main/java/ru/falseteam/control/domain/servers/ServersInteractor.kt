package ru.falseteam.control.domain.servers

import java.net.URL

interface ServersInteractor {
    suspend fun getPrimaryServer(): ServerInfo
    suspend fun setPrimaryServerUrl(url: URL)
}