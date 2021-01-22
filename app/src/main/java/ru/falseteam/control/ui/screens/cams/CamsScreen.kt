package ru.falseteam.control.ui.screens.cams

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import ru.falseteam.control.BuildConfig
import ru.falseteam.control.R
import ru.falseteam.control.di.kodeinViewModel
import ru.falseteam.control.ui.green900
import ru.falseteam.control.ui.red900
import ru.falseteam.control.ui.screens.Screen
import ru.falseteam.control.ui.screens.main.AmbientNavigation
import ru.falseteam.control.ui.screens.navigate

@Composable
fun CamsScreen(viewModel: CamsViewModel = kodeinViewModel()) {
    val camsState = viewModel.camsUi
        .collectAsState(initial = null)

    val navigation = AmbientNavigation.current
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navigation.navigate(Screen.AddCamera) }) {
                Image(vectorResource(id = R.drawable.ic_add))
            }
        },
    ) {
        val cams = camsState.value
        if (cams != null) {
            CamsList(cams)
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun CameraCard(camera: CameraUiModel) {
    val navigation = AmbientNavigation.current
    Card(
        modifier = Modifier
            .padding(8.dp, 6.dp)
            .clickable(onClick = { navigation.navigate(Screen.Livestream(camera.id)) })
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(12.dp, 12.dp)
        ) {
            Row {
                Text(
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.h6,
                    text = camera.name
                )
                IsCameraConnectedText(isConnected = camera.isConnected)
            }

            Row {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "Host: ${camera.address}"
                )
                if (BuildConfig.DEBUG) {
                    Text(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = "id: ${camera.id}",
                        style = MaterialTheme.typography.body2
                    )
                }
            }
        }
    }
}

@Composable
private fun IsCameraConnectedText(isConnected: Boolean) {
    if (isConnected) {
        Text(text = "Connected", color = green900)
    } else {
        Text(text = "Disconnected", color = red900)
    }
}

@Composable
private fun CamsList(cams: List<CameraUiModel>) {
    // TODO replace with recycler
    ScrollableColumn {
        cams.forEach { CameraCard(camera = it) }
    }
}