package ru.falseteam.control.api.rsub

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.CameraDTO

interface CamsRSub {
    fun observeAll(): Flow<List<CameraDTO>>
}