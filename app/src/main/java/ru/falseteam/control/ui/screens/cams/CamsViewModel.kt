package ru.falseteam.control.ui.screens.cams

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.combine
import ru.falseteam.control.api.dto.CameraStatusDTO
import ru.falseteam.control.domain.cams.CamsInteractor

class CamsViewModel(
    private val camsInteractor: CamsInteractor,
) : ViewModel() {
    val camsUi = combine(
        camsInteractor.observeAll(),
        camsInteractor.observerStatus()
    ) { cams, camsStatus ->
        cams.map {
            val isConnected = camsStatus[it.id] is CameraStatusDTO.Connected
            CameraUiModel(
                it.name,
                "${it.address}:${it.port}",
                isConnected
            )
        }
    }
}