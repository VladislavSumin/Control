package ru.falseteam.control.server.domain.records

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.withContext
import ru.falseteam.control.api.dto.CameraRecordDto
import ru.falseteam.control.server.database.CameraRecord
import ru.falseteam.control.server.database.CameraRecordQueries

class RecordsInteractorImpl(
    private val cameraRecordQueries: CameraRecordQueries,
) : RecordsInteractor {
    private val allObservable = cameraRecordQueries.selectAll()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { list -> list.map { it.toDTO() } }
        .shareIn(
            scope = GlobalScope,
            started = SharingStarted.WhileSubscribed(replayExpirationMillis = 0),
            replay = 1
        )

    override fun observeAll(): Flow<List<CameraRecordDto>> = allObservable

    override suspend fun insert(cameraRecord: CameraRecordDto) = withContext(Dispatchers.IO) {
        cameraRecordQueries.insert(cameraRecord.toEntity())
    }

    private fun CameraRecordDto.toEntity(): CameraRecord {
        return CameraRecord(
            id = id,
            cameraId = cameraId,
        )
    }


    private fun CameraRecord.toDTO(): CameraRecordDto {
        return CameraRecordDto(
            id = id,
            cameraId = cameraId,
        )
    }
}