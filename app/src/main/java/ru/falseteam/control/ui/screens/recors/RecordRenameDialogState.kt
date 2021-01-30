package ru.falseteam.control.ui.screens.recors

import kotlinx.coroutines.flow.MutableStateFlow

sealed class RecordRenameDialogState {
    object Hide : RecordRenameDialogState()

    abstract class Show : RecordRenameDialogState() {
        abstract val recordDTO: RecordUiModel
        abstract val name: MutableStateFlow<String>
    }

    data class Open(
        override val recordDTO: RecordUiModel,
        override val name: MutableStateFlow<String> = MutableStateFlow(recordDTO.name ?: "")
    ) : Show()

    data class Applying(
        override val recordDTO: RecordUiModel,
        override val name: MutableStateFlow<String> = MutableStateFlow(recordDTO.name ?: "")
    ) : Show()

    data class Error(
        override val recordDTO: RecordUiModel,
        override val name: MutableStateFlow<String> = MutableStateFlow(recordDTO.name ?: "")
    ) : Show()

}