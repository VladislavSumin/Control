package ru.falseteam.control.ui.screens.recors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.falseteam.control.domain.records.RecordsInteractor

class RecordsViewModel(
    private val recordsInteractor: RecordsInteractor
) : ViewModel() {
    val state = MutableStateFlow<RecordsState>(RecordsState.Loading)

    private val forceUpdate = MutableSharedFlow<Unit>(1)

    init {
        viewModelScope.launch {
            launch { forceUpdate.emit(Unit) }
            println("b")
            forceUpdate.collectLatest {
                println("a")
                request()
            }
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
            state.emit(RecordsState.Error(e.message ?: "No error message"))
        }
    }

    fun forceUpdate() {
        viewModelScope.launch {
            println("")
            forceUpdate.emit(Unit)
        }
    }
}