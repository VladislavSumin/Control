package ru.falseteam.control.ui.screens.addserver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.falseteam.control.domain.servers.ServersInteractor
import java.net.URL

class AddServerViewModel(
    private val serversInteractor: ServersInteractor,
) : ViewModel() {
    val state = MutableStateFlow(
        if (serversInteractor.isPrimaryServerSet()) AddServerState.Success
        else AddServerState.Input
    )

    fun onClickEnter(url: String) {
        viewModelScope.launch {
            serversInteractor.setPrimaryServerUrl(URL(url))
            state.emit(AddServerState.Success)
        }
    }
}