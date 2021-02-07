package ru.falseteam.control.server.rsub

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.control.api.dto.CameraRecordsInfoDTO
import ru.falseteam.control.api.dto.CameraStatusDTO
import ru.falseteam.control.api.rsub.CamsRSub
import ru.falseteam.control.server.domain.cams.CamsConnectionInteractor
import ru.falseteam.control.server.domain.cams.CamsInteractor
import ru.falseteam.control.server.domain.records.RecordsInteractor

class CamsRSubImpl(
    private val camsInteractor: CamsInteractor,
    private val recordsInteractor: RecordsInteractor,
    private val camsConnectionInteractor: CamsConnectionInteractor,
) : CamsRSub {
    override fun observeAll(): Flow<List<CameraDTO>> = camsInteractor.observeAll()
    override fun observeStatus(): Flow<Map<Long, CameraStatusDTO>> =
        camsConnectionInteractor.observeStatus()

    override fun observeRecordsInfo(): Flow<Map<Long, CameraRecordsInfoDTO>> =
        recordsInteractor.observeRecordsInfo()
}