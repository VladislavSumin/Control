package ru.falseteam.control.domain.records

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.CameraRecordDTO
import ru.falseteam.control.api.rsub.CamsRecordRSub

class RecordsInteractorImpl(
    private val camsRecordRSub: CamsRecordRSub,
) : RecordsInteractor {
    override fun observeAll(): Flow<List<CameraRecordDTO>> = camsRecordRSub.observeAll()
}