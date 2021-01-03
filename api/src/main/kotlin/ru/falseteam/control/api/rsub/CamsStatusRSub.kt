package ru.falseteam.control.api.rsub

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.rsub.RSubInterface

@RSubInterface("cams_status")
interface CamsStatusRSub {
    fun observeAll(): Flow<List<CamsStatusRSub>>
}