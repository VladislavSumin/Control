package ru.falseteam.control.ui.screens.entities

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import ru.falseteam.control.di.kodeinViewModel
import ru.falseteam.uikit.elements.UikitFullScreenProgressBar

@Composable
fun EntitiesScreen(viewModel: EntitiesViewModel = kodeinViewModel()) {
    when (val state = viewModel.state.collectAsState().value) {
        EntitiesState.Loading -> UikitFullScreenProgressBar()
        is EntitiesState.ShowResult -> ShowResultScreen(state)
    }
}

@Composable
private fun ShowResultScreen(state: EntitiesState.ShowResult) {
    LazyColumn {
        items(state.entities) {
            EntityCard(entity = it)
        }
    }
}

@Composable
private fun EntityCard(entity: EntityUiModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 4.dp),
        shape = RectangleShape
    ) {
        Column(modifier = Modifier.padding(16.dp, 8.dp)) {
            Text(text = entity.id)
            Text(text = entity.type)
            entity.states.forEach {
                Text(it.toString())
            }
        }
    }
}