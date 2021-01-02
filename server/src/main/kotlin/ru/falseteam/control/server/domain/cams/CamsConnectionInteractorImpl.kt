package ru.falseteam.control.server.domain.cams

import kotlinx.coroutines.delay

class CamsConnectionInteractorImpl(
    private val camsInteractor: CamsInteractor
) : CamsConnectionInteractor {
    override suspend fun processConnections() {
        while (true) {
            delay(10000)
        }
    }
}