package ru.falseteam.control.ui.screens.recors

import androidx.lifecycle.ViewModel
import ru.falseteam.control.domain.records.RecordsInteractor

class RecordsViewModel(
    private val recordsInteractor: RecordsInteractor
) : ViewModel() {
    val records = recordsInteractor.observeAll()
}