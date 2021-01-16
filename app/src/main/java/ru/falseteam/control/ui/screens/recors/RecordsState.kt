package ru.falseteam.control.ui.screens.recors

//TODO add no records exists state!
sealed class RecordsState {
    object Loading : RecordsState()
    data class Error(val error: String) : RecordsState()
    data class ShowResult(
        val records: List<RecordUiModel>,
    ) : RecordsState()
}