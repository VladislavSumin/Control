package ru.falseteam.control.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.falseteam.control.domain.cams.CamsInteractor

class AddCameraViewModel(
    private val camsInteractor: CamsInteractor,
) : ViewModel() {
    val state = MutableStateFlow<AddCameraState>(AddCameraState.Input)

    fun onClickAdd() {
        viewModelScope.launch {
            state.emit(AddCameraState.Loading)
        }
    }
}