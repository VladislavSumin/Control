package ru.falseteam.control.ui.screens.recors

import kotlinx.coroutines.flow.MutableStateFlow

sealed class RecordRenameDialogState {
    object Hide : RecordRenameDialogState()

    abstract class Show : RecordRenameDialogState() {
        abstract val record: RecordUiModel
        abstract val name: MutableStateFlow<String>
    }

    data class Edit(
        override val record: RecordUiModel,
        override val name: MutableStateFlow<String> = MutableStateFlow(record.name ?: "")
    ) : Show()

    data class Applying(
        override val record: RecordUiModel,
        override val name: MutableStateFlow<String> = MutableStateFlow(record.name ?: "")
    ) : Show()

    data class Error(
        override val record: RecordUiModel,
        override val name: MutableStateFlow<String> = MutableStateFlow(record.name ?: "")
    ) : Show()

}