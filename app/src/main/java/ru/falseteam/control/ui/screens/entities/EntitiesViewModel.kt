package ru.falseteam.control.ui.screens.entities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.falseteam.control.api.dto.EntityInfoDTO
import ru.falseteam.control.api.dto.EntityStateDto
import ru.falseteam.control.domain.entities.EntitiesInteractor

class EntitiesViewModel(
    private val entitiesInteractor: EntitiesInteractor,
) : ViewModel() {
    val state = MutableStateFlow<EntitiesState>(EntitiesState.Loading)

    init {
        viewModelScope.launch {
            combine(
                entitiesInteractor.observeAll(),
                entitiesInteractor.observeStates(),
                ::getEntitiesUiModel
            )
                .map { EntitiesState.ShowResult(it) }
                .collect { state.value = it }
        }
    }

    private suspend fun getEntitiesUiModel(
        entities: Map<String, EntityInfoDTO>,
        states: Map<String, Map<String, EntityStateDto>>
    ): List<EntityUiModel> {
        return entities.mapNotNull { (id, entity) ->
            val state = states[id] ?: return@mapNotNull null
            EntityUiModel(id, entity.type, state)
        }
    }
}