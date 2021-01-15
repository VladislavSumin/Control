package ru.falseteam.control.ui.screens.recors

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.map
import ru.falseteam.control.domain.records.RecordsInteractor

class RecordsViewModel(
    private val recordsInteractor: RecordsInteractor
) : ViewModel() {
    val records = recordsInteractor.observeAll().map {
        it.map { record ->
            RecordUiModel(record.id, record.name)
        }
    }
}