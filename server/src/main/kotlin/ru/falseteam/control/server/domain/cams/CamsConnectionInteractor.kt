package ru.falseteam.control.server.domain.cams

interface CamsConnectionInteractor {
    suspend fun processConnections()
}