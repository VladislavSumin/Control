package ru.falseteam.control.domain.records

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.RecordsApi
import ru.falseteam.control.api.dto.CameraRecordDTO
import ru.falseteam.control.api.rsub.CamsRecordRSub
import ru.falseteam.control.domain.servers.ServersInteractor

class RecordsInteractorImpl(
    private val camsRecordRSub: CamsRecordRSub,
    private val recordsApi: RecordsApi,
    private val serversInteractor: ServersInteractor,
) : RecordsInteractor {
    override fun observeAll(): Flow<List<CameraRecordDTO>> = camsRecordRSub.observeAll()
    override suspend fun getAll(): List<CameraRecordDTO> =
        recordsApi.getAll(serversInteractor.getPrimaryServer())

    override suspend fun getFiltered(
        onlyKeepForever: Boolean,
        onlyNamed: Boolean,
        startTime: Long?,
        endTime: Long?,
        reverse: Boolean,
        cams: List<Long>?
    ): List<CameraRecordDTO> =
        recordsApi.getFiltered(
            serversInteractor.getPrimaryServer(),
            onlyKeepForever,
            onlyNamed,
            startTime,
            endTime,
            reverse,
            cams
        )

    override suspend fun delete(id: Long) =
        recordsApi.delete(serversInteractor.getPrimaryServer(), id)

    override suspend fun setKeepForever(id: Long, keepForever: Boolean) =
        recordsApi.setKeepForever(serversInteractor.getPrimaryServer(), id, keepForever)

    override suspend fun rename(id: Long, name: String) =
        recordsApi.rename(serversInteractor.getPrimaryServer(), id, name)

    override suspend fun getRecordUri(id: Long): Uri {
        val server = serversInteractor.getPrimaryServer()
        return Uri.parse("${server.url}/api/v1/records/video/$id")
    }

    override suspend fun getPreviewUri(id: Long): Uri {
        val server = serversInteractor.getPrimaryServer()
        return Uri.parse("${server.url}/api/v1/records/preview/$id")
    }
}