package ru.falseteam.control.domain.cams

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class CamsInteractorImpl : CamsInteractor {
    override fun observeCams(): Flow<List<Camera>> = flowOf(
        listOf(
            Camera("Camera 1", "10.0.0.1"),
            Camera("Camera 2", "10.0.0.2"),
            Camera("Camera 3", "10.0.0.3"),
            Camera("Camera 4", "10.0.0.4"),
        )
    )

}