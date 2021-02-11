package ru.falseteam.control.ui.screens.entities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.falseteam.control.domain.entities.EntitiesInteractor

class EntitiesViewModel(
    private val entitiesInteractor: EntitiesInteractor,
) : ViewModel() {
    val state = MutableStateFlow<EntitiesState>(EntitiesState.Loading)

    init {
        viewModelScope.launch {
            entitiesInteractor.observeAll()
                .map { entities ->
                    entities.map { (id, entityInfo) -> EntityUiModel(id, entityInfo.type) }
                }
                .map { EntitiesState.ShowResult(it) }
                .collect { state.value = it }
        }
    }
}