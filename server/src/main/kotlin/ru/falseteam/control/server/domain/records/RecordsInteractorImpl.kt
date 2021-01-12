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
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.control.api.dto.CameraRecordDTO
import ru.falseteam.control.server.database.CameraRecord
import ru.falseteam.control.server.database.CameraRecordQueries
import ru.falseteam.control.server.domain.videoencoder.VideoEncodeInteractor
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class RecordsInteractorImpl(
    private val cameraRecordQueries: CameraRecordQueries,
    private val videoEncodeInteractor: VideoEncodeInteractor,
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

    override fun observeAll(): Flow<List<CameraRecordDTO>> = allObservable

    override suspend fun saveNewRecord(cameraDTO: CameraDTO, timestamp: Long, record: Path): Unit =
        withContext(Dispatchers.IO) {
            val duration = (videoEncodeInteractor.getDuration(record) * 1000).toLong()
            val cameraRecordDto = CameraRecordDTO(
                cameraId = cameraDTO.id,
                name = null,
                timestamp = timestamp,
                fileSize = record.toFile().length(),
                length = duration
            )

            val id = insert(cameraRecordDto)
            val recordLocation = Path.of("data/record/$id.mp4")
            Files.createDirectories(recordLocation.parent)
            Files.move(record, recordLocation)
        }

    override fun getRecord(id: Long): File {
        return File("data/record/$id.mp4")
    }

    private suspend fun insert(cameraRecord: CameraRecordDTO): Long = withContext(Dispatchers.IO) {
        cameraRecordQueries.transactionWithResult {
            cameraRecordQueries.insert(cameraRecord.toEntity())
            cameraRecordQueries.lastInsertRowId().executeAsOne()
        }
    }

    private fun CameraRecordDTO.toEntity(): CameraRecord {
        return CameraRecord(
            id = id,
            cameraId = cameraId,
            name = name,
            timestamp = timestamp,
            fileSize = fileSize,
            length = length,
        )
    }


    private fun CameraRecord.toDTO(): CameraRecordDTO {
        return CameraRecordDTO(
            id = id,
            cameraId = cameraId,
            name = name,
            timestamp = timestamp,
            fileSize = fileSize,
            length = length,
        )
    }
}