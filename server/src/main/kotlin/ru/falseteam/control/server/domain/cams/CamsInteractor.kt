package ru.falseteam.control.server.domain.cams

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.CameraDTO

interface CamsInteractor {
    fun observeCams(): Flow<List<CameraDTO>>
}