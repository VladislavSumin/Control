package ru.falseteam.control.ui.screens.entities

// TODO add no entities exists state!
sealed class EntitiesState {
    object Loading : EntitiesState()
    data class ShowResult(
        val entities: List<EntityUiModel>,
    ) : EntitiesState()
}