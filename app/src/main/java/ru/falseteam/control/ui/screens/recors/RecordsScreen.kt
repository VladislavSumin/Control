package ru.falseteam.control.ui.screens.recors

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
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

    }
}