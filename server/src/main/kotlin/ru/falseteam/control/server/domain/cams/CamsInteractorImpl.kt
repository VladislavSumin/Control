package ru.falseteam.control.server.domain.cams

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import ru.falseteam.control.api.dto.CameraDTO

class CamsInteractorImpl : CamsInteractor {
    private val cams = MutableStateFlow(
        listOf(
            CameraDTO(0, "Camera 1", "10.0.0.1"),
            CameraDTO(1, "Camera 2", "10.0.0.2"),
            CameraDTO(2, "Camera 3", "10.0.0.3"),
            CameraDTO(3, "Camera 4", "10.0.0.4"),
            CameraDTO(4, "Camera 5", "10.0.0.5"),
            CameraDTO(5, "Camera 6", "10.0.0.6"),
            CameraDTO(6, "Camera 7", "10.0.0.7"),
            CameraDTO(7, "Camera 8", "10.0.0.8"),
        )
    )


    override fun observeCams(): Flow<List<CameraDTO>> = cams
}