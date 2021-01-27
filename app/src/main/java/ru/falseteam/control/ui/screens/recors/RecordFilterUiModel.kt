package ru.falseteam.control.ui.screens.recors

import java.time.LocalDate

data class RecordFilterUiModel(
    val isOnlySaved: Boolean = false,
    val isOnlyNamed: Boolean = false,
    val date: LocalDate = LocalDate.now()
)