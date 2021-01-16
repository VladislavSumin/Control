package ru.falseteam.control.ui.screens.recors

sealed class RecordsState {
    object Loading : RecordsState()
    object Error : RecordsState()
    data class ShowResult(
        val records: List<RecordUiModel>,
    ) : RecordsState()
}