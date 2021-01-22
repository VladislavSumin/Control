package ru.falseteam.control.domain.servers

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import ru.falseteam.control.repository.servers.ServersRepository
import java.net.URL

class ServersInteractorImpl(
    private val serversRepository: ServersRepository,
) : ServersInteractor {
    private val primaryServerState = MutableStateFlow<ServerInfo?>(null)

    init {
        val defaultServer = serversRepository.defaultServer
        if (defaultServer != "") {
            runBlocking {
                primaryServerState.emit(ServerInfo(URL(defaultServer)))
            }
        }
    }

    override fun isPrimaryServerSet(): Boolean {
        return primaryServerState.value != null
    }

    override suspend fun getPrimaryServer(): ServerInfo = primaryServerState.first()!!

    override suspend fun setPrimaryServerUrl(url: URL) {
        serversRepository.defaultServer = url.toString()
        primaryServerState.emit(ServerInfo(url))
    }
}