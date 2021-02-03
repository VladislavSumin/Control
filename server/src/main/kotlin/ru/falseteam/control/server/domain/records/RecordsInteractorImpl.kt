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
import ru.falseteam.control.server.repository.ServerConfigurationRepository
import ru.falseteam.control.server.utils.toBoolean
import ru.falseteam.control.server.utils.toLong
import java.nio.file.Files
import java.nio.file.Path

class RecordsInteractorImpl(
    private val cameraRecordQueries: CameraRecordQueries,
    private val videoEncodeInteractor: VideoEncodeInteractor,
    private val serverConfigurationRepository: ServerConfigurationRepository,
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

    init {
        getRecordsPath().toFile().mkdirs()
        getRecordsTmpPath().toFile().mkdirs()
    }

    override fun observeAll(): Flow<List<CameraRecordDTO>> = allObservable

    override suspend fun getAll(): List<CameraRecordDTO> = withContext(Dispatchers.IO) {
        cameraRecordQueries.selectAll().executeAsList().map { it.toDTO() }
    }

    // TODO make custom sql request
    override suspend fun getFiltered(
        onlyKeepForever: Boolean,
        onlyNamed: Boolean,
        startTime: Long?,
        endTime: Long?,
        reverse: Boolean,
        cams: List<Long>?
    ): List<CameraRecordDTO> {
        return getAll().asSequence()
            .filter { !onlyKeepForever || it.keepForever }
            .filter { !onlyNamed || (it.name != null && it.name!!.isNotEmpty()) }
            .filter { startTime == null || startTime < it.timestamp }
            .filter { endTime == null || endTime >= it.timestamp }
            .filter { cams == null || it.cameraId in cams }
            .toList()
            .let { if (reverse) it.reversed() else it }
    }

    override suspend fun getById(id: Long): CameraRecordDTO? = withContext(Dispatchers.IO) {
        cameraRecordQueries.selectById(id).executeAsOneOrNull()?.toDTO()
    }

    override suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        getRecord(id).toFile().delete()
        getPreview(id).toFile().delete()
        cameraRecordQueries.deleteById(id)
    }

    // TODO throw exception if no records changed
    override suspend fun setKeepForever(id: Long, keepForever: Boolean) =
        withContext(Dispatchers.IO) {
            cameraRecordQueries.setKeepForever(
                keepForever = keepForever.toLong(),
                id = id
            )
        }

    override suspend fun rename(id: Long, name: String?) = withContext(Dispatchers.IO) {
        cameraRecordQueries.rename(name, id)
    }

    override suspend fun saveNewRecord(cameraDTO: CameraDTO, timestamp: Long, record: Path): Unit =
        withContext(Dispatchers.IO) {
            val duration = (videoEncodeInteractor.getDuration(record) * 1000).toLong()

            val cameraRecordDto = CameraRecordDTO(
                cameraId = cameraDTO.id,
                name = null,
                timestamp = timestamp,
                fileSize = record.toFile().length(),
                length = duration,
                keepForever = false,
            )
            val id = insert(cameraRecordDto)

            val recordLocation = getRecord(id)
            recordLocation.parent.toFile().mkdirs()
            val preview = getPreview(id)

            videoEncodeInteractor.createPreviewImage(record, preview)
            Files.move(record, recordLocation)
        }

    override fun getRecord(id: Long): Path {
        return getRecordFile(id, "mp4")
    }

    override fun getPreview(id: Long): Path {
        return getRecordFile(id, "jpg")
    }

    private fun getRecordFile(id: Long, ext: String): Path {
        return getRecordsPath().resolve("${id / 1000}").resolve("${id % 1000}.$ext")
    }

    override fun getRecordsTmpPath(): Path = Path.of(serverConfigurationRepository.recordsTmpPath)
    private fun getRecordsPath(): Path = Path.of(serverConfigurationRepository.recordsPath)

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
            keepForever = keepForever.toLong()
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
            keepForever = keepForever.toBoolean()
        )
    }
}