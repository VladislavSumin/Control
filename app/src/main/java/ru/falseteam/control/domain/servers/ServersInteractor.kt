package ru.falseteam.control.domain.servers

import java.net.URL

interface ServersInteractor {
    fun isPrimaryServerSet(): Boolean
    suspend fun getPrimaryServer(): ServerInfo
    suspend fun setPrimaryServerUrl(url: URL)
}