package ru.falseteam.control.server.domain.cams

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.control.server.database.Camera
import ru.falseteam.control.server.database.CameraQueries

class CamsInteractorImpl(
    cameraQueries: CameraQueries
) : CamsInteractor {
    private val allObservable = cameraQueries.selectAll()
        .asFlow()
        .mapToList()
        .flowOn(Dispatchers.IO)
        .map { list -> list.map { it.toDTO() } }
        .shareIn(
            scope = GlobalScope,
            started = SharingStarted.WhileSubscribed(replayExpirationMillis = 0),
            replay = 1
        )

    override fun observeAll(): Flow<List<CameraDTO>> = allObservable

    private fun Camera.toDTO(): CameraDTO {
        return CameraDTO(
            id = id,
            name = name,
            address = address,
            port = port.toInt()
        )
    }
}