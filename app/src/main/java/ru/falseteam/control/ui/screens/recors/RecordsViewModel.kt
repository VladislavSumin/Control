package ru.falseteam.control.ui.screens.recors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.falseteam.control.domain.cams.CamsInteractor
import ru.falseteam.control.domain.records.RecordsInteractor
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*

class RecordsViewModel(
    private val recordsInteractor: RecordsInteractor,
    private val camsInteractor: CamsInteractor,
) : ViewModel() {
    val state = MutableStateFlow<RecordsState>(RecordsState.Loading)
    val filterState = MutableStateFlow(RecordFilterUiModel())
    val filterCamsState = MutableStateFlow(RecordCamsFilterState(emptyList()))
    val renameDialogState = MutableStateFlow<RecordRenameDialogState>(RecordRenameDialogState.Hide)

    private val forceUpdate = MutableSharedFlow<Unit>(1)
    private val recordUpdateEvent = MutableSharedFlow<UpdateType>()

    private val dateFormatter = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault())

    private var renameJob: Job? = null

    init {
        viewModelScope.launch(Dispatchers.Default) {
            forceUpdate.emit(Unit)

            val cams = filterCamsState
                .map { state ->
                    state.cams
                        .asSequence()
                        .filter { it.isChecked }
                        .map { it.id }
                        .toList()
                }
                .distinctUntilChanged()

            combine(
                filterState,
                cams,
                forceUpdate,
                this@RecordsViewModel::request
            )
                .flattenConcat()
                .flatMapLatest(this@RecordsViewModel::applyUpdateEvents)
                .collect(state::emit)
        }

        viewModelScope.launch {
            val cams = camsInteractor.observeAll().first()
            filterCamsState.emit(
                RecordCamsFilterState(
                    cams.map { RecordCamsFilterState.Entity(it.id, it.name) }
                )
            )
        }
    }

    private suspend fun applyUpdateEvents(state: RecordsState): Flow<RecordsState> {
        return when (state) {
            is RecordsState.ShowResult -> {
                recordUpdateEvent.scan(state.records.toMutableList()) { records, update ->
                    when (update) {
                        is UpdateType.Update -> {
                            val record = update.recordUiModel
                            val position = records.indexOfFirst { it.id == record.id }
                            if (position >= 0) records[position] = record
                        }
                        is UpdateType.Delete -> {
                            val position = records.indexOfFirst { it.id == update.id }
                            if (position >= 0) records.removeAt(position)
                        }
                    }
                    records
                }
                    .map { RecordsState.ShowResult(it) }
                    .onStart { emit(state) }
            }
            else -> flowOf(state)
        }
    }

    private suspend fun request(
        recordFilterUiModel: RecordFilterUiModel,
        camsFilter: List<Long>,
        @Suppress("UNUSED_PARAMETER") forceUpdate: Unit,
    ): Flow<RecordsState> =
        flow {
            emit(RecordsState.Loading)
            try {
                val cams = camsInteractor.getAll()
                val records = recordsInteractor.getFiltered(
                    onlyKeepForever = recordFilterUiModel.isOnlySaved,
                    onlyNamed = recordFilterUiModel.isOnlyNamed,
                    startTime = recordFilterUiModel.date
                        .atStartOfDay(ZoneId.systemDefault())
                        .toEpochSecond() * 1000,
                    endTime = recordFilterUiModel.date
                        .plusDays(1)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toEpochSecond() * 1000,
                    reverse = true,
                    cams = if (camsFilter.isEmpty()) null
                    else camsFilter
                )
                    .map { record ->
                        val camera = cams.find { it.id == record.cameraId }
                        RecordUiModel(
                            id = record.id,
                            name = record.name,
                            cameraName = camera?.name,
                            date = dateFormatter.format(Date(record.timestamp)),
                            keepForever = record.keepForever,
                            uri = recordsInteractor.getRecordUri(record.id),
                            previewUri = recordsInteractor.getPreviewUri(record.id),
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
            recordUpdateEvent.emit(UpdateType.Update(newRecordState))
        }
    }

    fun deleteRecord(id: Long) {
        viewModelScope.launch {
            recordsInteractor.delete(id)
            recordUpdateEvent.emit(UpdateType.Delete(id))
        }
    }

    fun updateFilterModel(filterUiModel: RecordFilterUiModel) {
        viewModelScope.launch {
            filterState.emit(filterUiModel)
        }
    }

    fun showRenameDialog(recordUiModel: RecordUiModel) {
        viewModelScope.launch {
            renameDialogState.emit(RecordRenameDialogState.Edit(recordUiModel))
        }
    }

    fun hideRenameDialog() {
        viewModelScope.launch {
            renameJob?.cancel()
            renameDialogState.emit(RecordRenameDialogState.Hide)
        }
    }

    fun changeCamsFilterSelection(camera: RecordCamsFilterState.Entity) {
        viewModelScope.launch {
            val cams = filterCamsState.value.cams.toMutableList()
            val index = cams.indexOf(camera)
            if (index < 0) return@launch
            cams[index] = camera.copy(isChecked = !camera.isChecked)
            filterCamsState.emit(RecordCamsFilterState(cams))
        }
    }

    fun rename(recordUiModel: RecordUiModel, newName: String) {
        renameJob = viewModelScope.launch {
            renameDialogState.emit(
                RecordRenameDialogState.Applying(
                    recordUiModel,
                    MutableStateFlow(newName)
                )
            )
            try {
                recordsInteractor.rename(recordUiModel.id, newName)
                recordUpdateEvent.emit(
                    UpdateType.Update(recordUiModel.copy(name = newName))
                )
                renameDialogState.emit(RecordRenameDialogState.Hide)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                renameDialogState.emit(
                    RecordRenameDialogState.Error(
                        recordUiModel,
                        MutableStateFlow(newName),
                        e
                    )
                )
                // TODO add exceptions logging here!!!
            }
        }
    }

    private sealed class UpdateType {
        data class Update(val recordUiModel: RecordUiModel) : UpdateType()
        data class Delete(val id: Long) : UpdateType()
    }
}