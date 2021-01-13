package ru.falseteam.control.server.rsub

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.CameraRecordDTO
import ru.falseteam.control.api.rsub.CamsRecordRSub
import ru.falseteam.control.server.domain.records.RecordsInteractor

class CamsRecordRSubImpl(
    private val recordsInteractor: RecordsInteractor,
) : CamsRecordRSub {
    override fun observeAll(): Flow<List<CameraRecordDTO>> = recordsInteractor.observeAll()
}