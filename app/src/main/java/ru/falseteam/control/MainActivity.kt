package ru.falseteam.control

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.falseteam.control.ui.ControlTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ControlTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    CamsList(cams)
                }
            }
        }
    }
}

val cams = listOf(
    Camera("Camera 1", "10.0.0.1"),
    Camera("Camera 2", "10.0.0.2"),
    Camera("Camera 3", "10.0.0.3"),
    Camera("Camera 4", "10.0.0.4"),
)

data class Camera(val name: String, val address: String)

@Composable
fun CameraCard(camera: Camera) {
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
fun CamsList(cams: List<Camera>) {
    ScrollableColumn(
//        modifier = Modifier.padding(4.dp, 0.dp)
    ) {
        cams.forEach { CameraCard(camera = it) }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ControlTheme {
        CamsList(cams)
    }
}