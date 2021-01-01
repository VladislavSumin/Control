package ru.falseteam.control

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.kodein.di.instance
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.control.di.Kodein
import ru.falseteam.control.domain.cams.CamsInteractor
import ru.falseteam.control.domain.cams.CamsInteractorImpl
import ru.falseteam.control.ui.ControlTheme
import ru.falseteam.control.ui.screens.AddCameraScreen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ControlTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
//                    CamsList()
                    AddCameraScreen()
                }
            }
        }
    }
}

val camsIterator by Kodein.instance<CamsInteractor>()

@Composable
fun CameraCard(camera: CameraDTO) {
    Card(
        elevation = 2.dp,
        modifier = Modifier
            .padding(4.dp, 4.dp)
            .clickable(onClick = { /*TODO*/ })

    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Text(text = camera.name)
            Text(text = camera.address)
        }
    }
}

@Composable
fun CamsList() {
    val cams = camsIterator.observeAll()
        .collectAsState(initial = listOf(CameraDTO(0, "asf", "asfdf")))
    ScrollableColumn(
//        modifier = Modifier.padding(4.dp, 0.dp)
    ) {
        cams.value.forEach { CameraCard(camera = it) }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ControlTheme {
        CamsList()
    }
}