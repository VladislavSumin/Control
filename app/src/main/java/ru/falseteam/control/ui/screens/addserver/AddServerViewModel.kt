package ru.falseteam.control.ui.screens.addserver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.falseteam.control.domain.servers.ServersInteractor
import java.net.URL

class AddServerViewModel(
    private val serversInteractor: ServersInteractor,
) : ViewModel() {
    fun onClickEnter(url: String) {
        viewModelScope.launch {
            serversInteractor.setPrimaryServerUrl(URL(url))
        }
    }
}