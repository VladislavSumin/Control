package ru.falseteam.control.ui.screens.recors

import android.widget.CalendarView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.viewinterop.AndroidView
import ru.falseteam.uikit.elements.UikitCheckBoxListItem
import java.time.LocalDate

@Composable
fun RecordsFilterScreen(viewModel: RecordsViewModel) {
    val state = viewModel.filterState.collectAsState().value
    Column {
        CalendarFilter(state, viewModel)
        Divider()

        UikitCheckBoxListItem(
            text = "Только сохраненные",
            checked = state.isOnlySaved,
            onCheckedChange = { viewModel.updateFilterModel(state.copy(isOnlySaved = !state.isOnlySaved)) }
        )
        Divider()

        UikitCheckBoxListItem(
            text = "Только c именем",
            checked = state.isOnlyNamed,
            onCheckedChange = { viewModel.updateFilterModel(state.copy(isOnlyNamed = !state.isOnlyNamed)) }
        )
        Divider()

        CamsFilter(viewModel)
    }
}

@Composable
private fun CalendarFilter(state: RecordFilterUiModel, viewModel: RecordsViewModel) {
    Surface {
        val context = AmbientContext.current
        val calendar = remember {
            CalendarView(context).apply {
                setOnDateChangeListener { _, year, month, dayOfMonth ->
                    val date = LocalDate.of(year, month + 1, dayOfMonth)
                    val newState = state.copy(date = date)
                    viewModel.updateFilterModel(newState)
                }
            }
        }
        AndroidView(viewBlock = { calendar })
    }
}

@Composable
private fun CamsFilter(viewModel: RecordsViewModel) {
    val cams = viewModel.filterCamsState.collectAsState().value.cams
    LazyColumn {
        items(cams) { camera ->
            UikitCheckBoxListItem(
                text = camera.name,
                checked = camera.isChecked,
                onCheckedChange = { viewModel.changeCamsFilterSelection(camera) }
            )
            Divider()
        }
    }
}