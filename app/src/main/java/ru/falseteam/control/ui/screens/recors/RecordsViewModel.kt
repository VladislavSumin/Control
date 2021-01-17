package ru.falseteam.control.ui.screens.recors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
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
    val filterState = MutableStateFlow(RecordFilterUiModel())

    private val forceUpdate = MutableSharedFlow<Unit>(1)
    private val recordUpdateEvent = MutableSharedFlow<RecordUiModel>()

    private val dateFormatter = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault())

    init {
        viewModelScope.launch(Dispatchers.Default) {
            launch { forceUpdate.emit(Unit) }

            combine(
                filterState,
                forceUpdate,
            ) { filterState, _ -> filterState }
                .flatMapLatest(this@RecordsViewModel::request)
                .flatMapLatest(this@RecordsViewModel::applyUpdateEvents)
                .collect {
                    state.emit(it)
                }
        }
    }

    private suspend fun applyUpdateEvents(state: RecordsState): Flow<RecordsState> {
        return when (state) {
            is RecordsState.ShowResult -> {
                recordUpdateEvent.scan(state.records.toMutableList()) { records, newModel ->
                    val position = records.indexOfFirst { it.id == newModel.id }
                    if (position >= 0) records[position] = newModel
                    records
                }
                    .map { RecordsState.ShowResult(it) }
                    .onStart { emit(state) }
            }
            else -> flowOf(state)
        }
    }

    private suspend fun request(recordFilterUiModel: RecordFilterUiModel): Flow<RecordsState> =
        flow {
            emit(RecordsState.Loading)
            try {
                val cams = camsInteractor.getAll()
                val records = recordsInteractor.getFiltered(
                    onlyKeepForever = recordFilterUiModel.isOnlySaved,
                    onlyNamed = recordFilterUiModel.isOnlyNamed,
                )
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
                emit(RecordsState.ShowResult(records))
            } catch (e: Exception) {
                emit(RecordsState.Error(e.message ?: "No error message"))
            }
        }

    fun forceUpdate() {
        viewModelScope.launch {
            forceUpdate.emit(Unit)
        }
    }

    fun setKeepForever(recordUiModel: RecordUiModel) {
        viewModelScope.launch {
            recordsInteractor.setKeepForever(recordUiModel.id, !recordUiModel.keepForever)
            val newRecordState = recordUiModel.copy(keepForever = !recordUiModel.keepForever)
            recordUpdateEvent.emit(newRecordState)
        }
    }

    fun updateFilterModel(filterUiModel: RecordFilterUiModel) {
        viewModelScope.launch {
            filterState.emit(filterUiModel)
        }
    }
}