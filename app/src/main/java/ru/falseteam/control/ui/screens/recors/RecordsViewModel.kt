package ru.falseteam.control.ui.screens.recors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.falseteam.control.domain.cams.CamsInteractor
import ru.falseteam.control.domain.records.RecordsInteractor
import java.text.SimpleDateFormat
import java.util.*

class RecordsViewModel(
    private val recordsInteractor: RecordsInteractor,
    private val camsInteractor: CamsInteractor,
) : ViewModel() {
    val state = MutableStateFlow<RecordsState>(RecordsState.Loading)

    private val forceUpdate = MutableSharedFlow<Unit>(1)

    private val dateFormatter = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault())

    init {
        viewModelScope.launch {
            launch { forceUpdate.emit(Unit) }
            forceUpdate.collectLatest {
                request()
            }
        }
    }

    private suspend fun request() {
        state.emit(RecordsState.Loading)
        try {
            val cams = camsInteractor.getAll()
            val records = recordsInteractor.getAll()
                .map { record ->
                    val camera = cams.find { it.id == record.cameraId }
                    RecordUiModel(
                        id = record.id,
                        name = record.name,
                        cameraName = camera?.name,
                        date = dateFormatter.format(Date(record.timestamp)),
                        keepForever = record.keepForever
                    )
                }
            state.emit(RecordsState.ShowResult(records))
        } catch (e: Exception) {
            state.emit(RecordsState.Error(e.message ?: "No error message"))
        }
    }

    fun forceUpdate() {
        viewModelScope.launch {
            forceUpdate.emit(Unit)
        }
    }

    fun setKeepForever(recordId: Long, keepForever: Boolean) {
        viewModelScope.launch {
            recordsInteractor.setKeepForever(recordId, keepForever)
        }
    }
}