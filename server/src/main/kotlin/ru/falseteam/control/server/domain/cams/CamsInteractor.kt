package ru.falseteam.control.server.domain.cams

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.CameraDTO

interface CamsInteractor {
    fun observeAll(): Flow<List<CameraDTO>>
    suspend fun getAll(): List<CameraDTO>
    suspend fun getById(id: Long): CameraDTO?
    suspend fun insert(camera: CameraDTO)
}