package ru.falseteam.control.ui.screens.cams

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.combine
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.control.api.dto.CameraRecordsInfoDTO
import ru.falseteam.control.api.dto.CameraStatusDTO
import ru.falseteam.control.domain.cams.CamsInteractor

class CamsViewModel(
    camsInteractor: CamsInteractor,
) : ViewModel() {
    val camsUi = combine(
        camsInteractor.observeAll(),
        camsInteractor.observeStatus(),
        camsInteractor.observeRecordsInfo()
    ) { cams, camsStatus, recordsInfos ->
        cams
            .asSequence()
            .map {
                val cameraStatus = camsStatus[it.id] ?: return@map null
                val recordInfo = recordsInfos[it.id] ?: return@map null
                createUiModel(it, cameraStatus, recordInfo)
            }
            .filterNotNull()
            .toList()
    }

    private fun createUiModel(
        camera: CameraDTO,
        cameraStatus: CameraStatusDTO,
        recordsInfo: CameraRecordsInfoDTO
    ): CameraUiModel {
        val isConnected = cameraStatus is CameraStatusDTO.Connected
        return CameraUiModel(
            camera.id,
            camera.name,
            "${camera.address}:${camera.port}",
            isConnected,
            createRecordInfoUiModel(recordsInfo.allRecords),
            createRecordInfoUiModel(recordsInfo.keepForeverRecords),
        )
    }

    private fun createRecordInfoUiModel(recordsInfo: CameraRecordsInfoDTO.RecordsCategoryInfo): RecordsInfoUiModel {
        return RecordsInfoUiModel(
            totalCount = recordsInfo.totalCount.toString(),
            totalLength = recordsInfo.totalLength.toString(),
            totalSize = recordsInfo.totalSize.toString(),
        )
    }
}