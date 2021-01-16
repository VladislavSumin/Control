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
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import ru.falseteam.control.R
import ru.falseteam.control.di.kodeinViewModel
import ru.falseteam.control.ui.screens.Screen
import ru.falseteam.control.ui.screens.navigate

@Composable
fun CamsScreen(navController: NavController, viewModel: CamsViewModel = kodeinViewModel()) {
    val camsState = viewModel.camsUi
        .collectAsState(initial = null)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.AddCamera) }) {
                Image(vectorResource(id = R.drawable.ic_add))
            }
        },
    ) {
        val cams = camsState.value
        if (cams != null) {
            CamsList(navController, cams)
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
private fun CameraCard(navController: NavController, camera: CameraUiModel) {
    Card(
        elevation = 2.dp,
        modifier = Modifier
            .padding(4.dp, 4.dp)
            .clickable(onClick = { navController.navigate(Screen.Livestream(camera.id)) })
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(8.dp, 4.dp)
        ) {
            Text(text = camera.name)
            Text(text = camera.address)
            Text(text = camera.isConnected.toString())
        }
    }
}

@Composable
private fun CamsList(navController: NavController, cams: List<CameraUiModel>) {
    //TODO replace with recycler
    ScrollableColumn {
        cams.forEach { CameraCard(navController = navController, camera = it) }
    }
}