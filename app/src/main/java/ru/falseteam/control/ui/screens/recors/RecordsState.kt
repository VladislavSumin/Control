package ru.falseteam.control.ui.screens.recors

// TODO add no records exists state!
sealed class RecordsState {
    object Loading : RecordsState()
    data class Error(val error: String) : RecordsState()
    data class ShowResult(
        val records: List<RecordUiModel>,
    ) : RecordsState() {
        override fun equals(other: Any?): Boolean {
            return super.equals(other)
        }

        override fun hashCode(): Int {
            return super.hashCode()
        }
    }
}