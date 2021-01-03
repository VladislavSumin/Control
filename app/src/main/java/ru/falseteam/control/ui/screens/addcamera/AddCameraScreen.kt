package ru.falseteam.control.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.control.di.kodeinViewModel
import ru.falseteam.control.ui.PrimaryAccentButton
import ru.falseteam.control.ui.screens.addcamera.AddCameraState
import ru.falseteam.control.ui.screens.addcamera.AddCameraViewModel

@Composable
fun AddCameraScreen(
    navController: NavController,
    viewModel: AddCameraViewModel = kodeinViewModel()
) {
    val state = viewModel.state.collectAsState().value
    val isLoading = state is AddCameraState.Loading

    val (name, setName) = savedInstanceState { "" }
    val (address, setAddress) = savedInstanceState { "" }
    val (port, setPort) = savedInstanceState { "" }

    if (state is AddCameraState.Success) {
        navController.popBackStack()
        return
    }

    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp, 16.dp)
    ) {
        TextField(
            value = name,
            onValueChange = { setName(it) },
            label = { Text(text = "Camera name") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 4.dp)
        )

        TextField(
            value = address,
            onValueChange = { setAddress(it) },
            label = { Text(text = "Address") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 4.dp)
        )

        TextField(
            value = port,
            onValueChange = { setPort(it) },
            label = { Text(text = "Port") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 4.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        PrimaryAccentButton(
            onClick = {
                viewModel.onClickAdd(
                    CameraDTO(
                        id = 0,
                        name = name,
                        address = address,
                        port = port.toInt()
                    )
                )
            },
            text = "Add camera",
            showProgress = isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 4.dp)
        )
    }
}