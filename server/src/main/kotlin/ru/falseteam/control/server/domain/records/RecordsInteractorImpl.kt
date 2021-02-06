package ru.falseteam.control.server.domain.records

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.control.api.dto.CameraRecordDTO
import ru.falseteam.control.api.dto.CameraRecordsInfoDTO
import ru.falseteam.control.server.domain.videoencoder.VideoEncodeInteractor
import ru.falseteam.control.server.repository.CameraRecordsRepository
import ru.falseteam.control.server.repository.ServerConfigurationRepository
import java.nio.file.Files
import java.nio.file.Path

class RecordsInteractorImpl(
    private val cameraRecordsRepository: CameraRecordsRepository,
    private val videoEncodeInteractor: VideoEncodeInteractor,
    private val serverConfigurationRepository: ServerConfigurationRepository,
) : RecordsInteractor {
    init {
        getRecordsPath().toFile().mkdirs()
        getRecordsTmpPath().toFile().mkdirs()
    }

    override suspend fun getAll(): List<CameraRecordDTO> = cameraRecordsRepository.getAll()
    override fun observeAll(): Flow<List<CameraRecordDTO>> = cameraRecordsRepository.observeAll()

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

    override suspend fun getById(id: Long): CameraRecordDTO? = cameraRecordsRepository.getOrNull(id)

    override suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        getRecord(id).toFile().delete()
        getPreview(id).toFile().delete()
        cameraRecordsRepository.delete(id)
    }

    override suspend fun setKeepForever(id: Long, keepForever: Boolean) =
        cameraRecordsRepository.setKeepForever(id, keepForever)

    override suspend fun rename(id: Long, name: String?) = cameraRecordsRepository.rename(id, name)

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
            val id = cameraRecordsRepository.insert(cameraRecordDto)

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

    override fun observeRecordsInfo(): Flow<List<CameraRecordsInfoDTO>> {
        return TODO()
    }
}