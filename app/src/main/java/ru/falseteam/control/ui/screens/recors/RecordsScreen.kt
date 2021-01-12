package ru.falseteam.control.ui.screens.recors

import android.net.Uri
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import ru.falseteam.control.api.dto.CameraRecordDTO
import ru.falseteam.control.di.kodeinViewModel

@Composable
fun RecordsScreen(navController: NavController, viewModel: RecordsViewModel = kodeinViewModel()) {
    val records = viewModel.records.collectAsState(initial = null).value
    if (records != null) {
        RecordsList(records = records)
    } else {
        //TODO make global function like this
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
private fun RecordsList(records: List<CameraRecordDTO>) {
    ScrollableColumn {
        records.forEach { RecordCard(record = it) }
    }
}

@Composable
private fun RecordCard(record: CameraRecordDTO) {
    Card(
        shape = RectangleShape,
        elevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            VideoRecord(record.id)
            Text(text = record.timestamp.toString())
            Text(text = record.fileSize.toString())
            Text(text = record.length.toString())
        }
    }
}

@Composable
private fun VideoRecord(id: Long) {
    Surface {
        val context = AmbientContext.current
        val videoView = remember {
            val controller = MediaController(context)
            val video = CustomVideoView(context)

            video.setMediaController(controller)
            video.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            controller.setAnchorView(video)

            video
        }

        AndroidView(
            modifier = Modifier.background(Color.Red),
            viewBlock = { videoView }) {
            it.setVideoURI(Uri.parse("http://10.0.0.56:8080/api/v1/record_video/$id"))
            //it.requestFocus()
            //it.start()
        }
    }
}