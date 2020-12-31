package ru.falseteam.control.domain.cams

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.CameraDTO

interface CamsInteractor {
    fun observeAll(): Flow<List<CameraDTO>>
}