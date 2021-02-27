package ru.falseteam.control.ui.screens.cams

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import ru.falseteam.control.BuildConfig
import ru.falseteam.control.R
import ru.falseteam.control.di.kodeinViewModel
import ru.falseteam.uikit.green900
import ru.falseteam.uikit.red900
import ru.falseteam.control.ui.screens.Screen
import ru.falseteam.control.ui.screens.main.LocalNavigation
import ru.falseteam.control.ui.screens.navigate
import ru.falseteam.uikit.elements.UikitFullScreenProgressBar

@Composable
fun CamsScreen(viewModel: CamsViewModel = kodeinViewModel()) {
    val cams = viewModel.camsUi.collectAsState(initial = null).value
    val navigation = LocalNavigation.current
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navigation.navigate(Screen.AddCamera) }) {
                Image(ImageVector.vectorResource(id = R.drawable.ic_add), "add camera")
            }
        },
    ) {
        if (cams != null) CamsList(cams)
        else UikitFullScreenProgressBar()
    }
}

@Composable
private fun CamsList(cams: List<CameraUiModel>) {
    LazyColumn {
        this.items(cams) { CameraCard(camera = it) }
    }
}

@Composable
private fun CameraCard(camera: CameraUiModel) {
    val navigation = LocalNavigation.current
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

            Divider(modifier = Modifier.padding(0.dp, 8.dp))
            RecordsInfo(
                icon = R.drawable.ic_sigma,
                recordsInfoUiModel = camera.allRecordsInfo
            )
            Divider(modifier = Modifier.padding(0.dp, 8.dp))
            RecordsInfo(
                icon = R.drawable.ic_star_filled,
                recordsInfoUiModel = camera.favouriteRecordsInfo,
            )

        }
    }
}

@Composable
private fun RecordsInfo(@DrawableRes icon: Int, recordsInfoUiModel: RecordsInfoUiModel) {
    Row {
        Icon(
            imageVector = ImageVector.vectorResource(id = icon),
            contentDescription = "",
            modifier = Modifier.padding(0.dp, 0.dp, 8.dp, 0.dp)
        )

        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_camera_records),
            contentDescription = "total count"
        )
        Text(text = recordsInfoUiModel.totalCount,
            Modifier
                .weight(.75f)
                .padding(4.dp, 0.dp))
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_time),
            contentDescription = "total length"
        )
        Text(text = recordsInfoUiModel.totalLength,
            Modifier
                .weight(1f)
                .padding(4.dp, 0.dp))

        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_save),
            contentDescription = "total size"
        )
        Text(text = recordsInfoUiModel.totalSize,
            Modifier
                .weight(1f)
                .padding(4.dp, 0.dp))
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