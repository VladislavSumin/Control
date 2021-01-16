package ru.falseteam.control.ui.screens.recors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.falseteam.control.domain.records.RecordsInteractor

class RecordsViewModel(
    private val recordsInteractor: RecordsInteractor
) : ViewModel() {
    val state = MutableStateFlow<RecordsState>(RecordsState.Loading)

    init {
        viewModelScope.launch {
            request()
        }
    }

    private suspend fun request() {
        state.emit(RecordsState.Loading)
        try {
            val records = recordsInteractor.getAll()
                .map { camera ->
                    RecordUiModel(
                        id = camera.id,
                        name = camera.name,
                    )
                }
            state.emit(RecordsState.ShowResult(records))
        } catch (e: Exception) {
            //TODO add error logging
            state.emit(RecordsState.Error)
        }
    }

}