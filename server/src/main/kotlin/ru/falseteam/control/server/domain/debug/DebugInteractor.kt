package ru.falseteam.control.server.domain.debug

interface DebugInteractor {
    fun configure()
    suspend fun run()
}