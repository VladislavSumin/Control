package ru.falseteam.control.server.repository

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.withContext
import ru.falseteam.control.api.dto.CameraRecordDTO
import ru.falseteam.control.server.database.CameraRecord
import ru.falseteam.control.server.database.CameraRecordQueries
import ru.falseteam.control.server.utils.toBoolean
import ru.falseteam.control.server.utils.toLong

class CameraRecordsRepositoryImpl(
    private val cameraRecordQueries: CameraRecordQueries,
) : CameraRecordsRepository {
    private val allObservable = cameraRecordQueries.selectAll()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { it.toDTO() }
        .shareIn(
            scope = GlobalScope,
            started = SharingStarted.WhileSubscribed(replayExpirationMillis = 0),
            replay = 1
        )


    override suspend fun getAll(): List<CameraRecordDTO> = withContext(Dispatchers.IO) {
        cameraRecordQueries.selectAll().executeAsList().map { it.toDTO() }
    }

    override fun observeAll(): Flow<List<CameraRecordDTO>> = allObservable

    override suspend fun getOrNull(id: Long): CameraRecordDTO? = withContext(Dispatchers.IO) {
        cameraRecordQueries.selectById(id).executeAsOneOrNull()?.toDTO()
    }

    override suspend fun get(id: Long): CameraRecordDTO = withContext(Dispatchers.IO) {
        cameraRecordQueries.selectById(id).executeAsOne().toDTO()
    }

    override suspend fun insert(record: CameraRecordDTO): Long = withContext(Dispatchers.IO) {
        cameraRecordQueries.transactionWithResult {
            cameraRecordQueries.insert(record.toEntity())
            cameraRecordQueries.lastInsertRowId().executeAsOne()
        }
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

    override suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        cameraRecordQueries.deleteById(id)
    }

    private fun List<CameraRecord>.toDTO() = map { it.toDTO() }
    private fun List<CameraRecordDTO>.toEntity() = map { it.toEntity() }

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