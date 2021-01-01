package ru.falseteam.control.ui.screens

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.kodein.di.instance
import ru.falseteam.control.R
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.control.di.Kodein
import ru.falseteam.control.domain.cams.CamsInteractor

@Composable
@Preview(showBackground = true)
fun CamsScreen() {
    val camsInteractor: CamsInteractor by Kodein.instance()
    val camsState = camsInteractor.observeAll()
        .collectAsState(initial = null)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { /*TODO*/ }) {
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
private fun CameraCard(camera: CameraDTO) {
    Card(
        elevation = 2.dp,
        modifier = Modifier
            .padding(4.dp, 4.dp)
            .clickable(onClick = { /*TODO*/ })

    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(8.dp, 4.dp)
        ) {
            Text(text = camera.name)
            Text(text = camera.address)
        }
    }
}

@Composable
private fun CamsList(cams: List<CameraDTO>) {
    ScrollableColumn {
        cams.forEach { CameraCard(camera = it) }
    }
}