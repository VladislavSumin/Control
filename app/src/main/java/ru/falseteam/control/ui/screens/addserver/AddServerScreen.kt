package ru.falseteam.control.ui.screens.addserver

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.falseteam.control.di.kodeinViewModel
import ru.falseteam.uikit.elements.UiKitPrimaryAccentButton
import ru.falseteam.control.ui.screens.Screen
import ru.falseteam.control.ui.screens.main.AmbientNavigation
import ru.falseteam.control.ui.screens.navigate

@Composable
fun AddServerScreen(viewModel: AddServerViewModel = kodeinViewModel()) {
    when (viewModel.state.collectAsState().value) {
        AddServerState.Input -> InputState(viewModel = viewModel)
        AddServerState.Success -> {
            AmbientNavigation.current.navigate(Screen.Cams) {
                popUpTo = 0
                launchSingleTop = true
            }
        }
    }
}

@Composable
private fun InputState(viewModel: AddServerViewModel) {
    val (url, setUrl) = savedInstanceState { "" }
    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp, 16.dp)
    ) {
        TextField(
            value = url,
            onValueChange = { setUrl(it) },
            label = { Text(text = "Server url") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 4.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        UiKitPrimaryAccentButton(
            onClick = {
                viewModel.onClickEnter(url)
            },
            text = "Enter",
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 4.dp)
        )
    }
}
