package ru.falseteam.control.ui.screens.entities

import androidx.lifecycle.ViewModel
import ru.falseteam.control.domain.entities.EntitiesInteractor

class EntitiesViewModel(
    private val entitiesInteractor: EntitiesInteractor,
) : ViewModel() {
}