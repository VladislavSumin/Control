package ru.falseteam.control.ui.screens.recors

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.runBlocking
import ru.falseteam.control.R
import ru.falseteam.control.di.kodeinViewModel
import ru.falseteam.uikit.elements.UiKitPrimaryButton
import ru.falseteam.uikit.elements.UikitFullScreenProgressBar
import ru.falseteam.uikit.red900

@Composable
fun RecordsScreen(viewModel: RecordsViewModel = kodeinViewModel()) {
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(scaffoldState, viewModel) },
        drawerContent = { RecordsFilterScreen(viewModel) },
    ) { Content(viewModel) }
}

@Composable
private fun Content(viewModel: RecordsViewModel) {
    ChangeNameScreen(viewModel)

    val context = AmbientContext.current
    val playerCache = remember {
        RecordsPlayerCache(context)
    }

    when (val state = viewModel.state.collectAsState().value) {
        RecordsState.Loading -> UikitFullScreenProgressBar()
        is RecordsState.Error -> ErrorScreen(state, viewModel)
        is RecordsState.ShowResult -> ShowResultScreen(state, viewModel, playerCache)
    }
}

@Composable
private fun TopBar(scaffoldState: ScaffoldState, viewModel: RecordsViewModel) {
    TopAppBar {
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            modifier = Modifier.align(Alignment.CenterVertically),
            onClick = { scaffoldState.drawerState.open() }
        ) {
            Icon(vectorResource(id = R.drawable.ic_filter), "filter")
        }
        IconButton(
            modifier = Modifier.align(Alignment.CenterVertically),
            onClick = { viewModel.forceUpdate() }
        ) {
            Icon(vectorResource(id = R.drawable.ic_refresh), "refresh")
        }
    }
}

@Composable
private fun ErrorScreen(state: RecordsState.Error, viewModel: RecordsViewModel) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            Text(
                text = "Не удалось загрузить записи с сервера",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = "Ошибка: ${state.error}",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            UiKitPrimaryButton(
                text = "Retry",
                onClick = { viewModel.forceUpdate() },
                modifier = Modifier
                    .padding(0.dp, 16.dp, 0.dp, 0.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun ShowResultScreen(
    state: RecordsState.ShowResult,
    viewModel: RecordsViewModel,
    playerCache: RecordsPlayerCache
) {
    RecordsList(state.records, viewModel, playerCache)
}

@Composable
fun ChangeNameScreen(viewModel: RecordsViewModel) {
    val state = viewModel.renameDialogState.collectAsState().value
    if (state is RecordRenameDialogState.Show) ChangeNameDialog(state, viewModel)
}

@Composable
private fun ChangeNameDialog(state: RecordRenameDialogState.Show, viewModel: RecordsViewModel) {
    val name = state.name.collectAsState().value
    Dialog(
        onDismissRequest = { viewModel.hideRenameDialog() },
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(16.dp, 16.dp)) {
                Text(text = "Имя записи", style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = name,
                    readOnly = state is RecordRenameDialogState.Applying,
                    singleLine = true,
                    onValueChange = { runBlocking { state.name.emit(it) } },
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                // TODO add animation
                if (state is RecordRenameDialogState.Error) {
                    Text(
                        text = "Error: ${state.exception.message}",
                        color = red900
                    ) // TODO use theme
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(onClick = { viewModel.hideRenameDialog() }) {
                        Text(text = "Cancel")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    UiKitPrimaryButton(
                        onClick = { viewModel.rename(state.record, name) },
                        text = "Save",
                        showProgress = state is RecordRenameDialogState.Applying
                    )
                }
            }
        }
    }
}

@Composable
private fun RecordsList(
    records: List<RecordUiModel>,
    viewModel: RecordsViewModel,
    playerCache: RecordsPlayerCache,
) {
    LazyColumn {
        items(records) { record ->
            RecordCard(record = record, playerCache, viewModel)
        }
    }
}

@Composable
private fun RecordCard(
    record: RecordUiModel,
    playerCache: RecordsPlayerCache,
    viewModel: RecordsViewModel
) {
    Card(
        shape = RectangleShape,
        elevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 6.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row {
                Text(
                    text = record.name ?: "Без названия...",
                    style = if (record.name != null) MaterialTheme.typography.body1
                    else MaterialTheme.typography.body2,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(16.dp, 0.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { viewModel.showRenameDialog(record) }) {
                    Icon(vectorResource(id = R.drawable.ic_edit), "rename")
                }
            }

            val (isPlayerActive, setIsPlayerActive) = remember { mutableStateOf(false) }
            if (isPlayerActive) {
                VideoRecord(record, playerCache)
            } else {
                Preview(record = record) { setIsPlayerActive(true) }
            }

            Row(
                modifier = Modifier.padding(16.dp, 8.dp)
            ) {
                Text(
                    text = record.cameraName ?: "Unknown camera",
                    modifier = Modifier.weight(1f)
                )
                Text(text = record.date)
            }

            Divider(modifier = Modifier.padding(8.dp, 0.dp))
            Row {
                IconButton(onClick = { viewModel.setKeepForever(record) }) {
                    if (record.keepForever) Icon(
                        vectorResource(id = R.drawable.ic_star_filled), "start set",
                        tint = Color.Unspecified
                    )
                    else Icon(vectorResource(id = R.drawable.ic_star), "start not set")
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { viewModel.deleteRecord(record.id) }) {
                    Icon(vectorResource(id = R.drawable.ic_delete), "delete", tint = red900)
                }
            }
        }
    }
}

@Composable
private fun Preview(record: RecordUiModel, onlClick: () -> Unit) {
    val image = loadPicture(uri = record.previewUri)
    Box(
        Modifier
            .fillMaxWidth()
            .aspectRatio(16 / 9.0f)
    ) {
        when (image) {
            PictureLoadState.Loading -> UikitFullScreenProgressBar()
            is PictureLoadState.Success -> {
                Image(
                    bitmap = image.resource.asImageBitmap(),
                    contentDescription = "record preview",
                    Modifier.clickable(onClick = onlClick)
                )
                FloatingActionButton(
                    onClick = onlClick,
                    backgroundColor = Color(0x80000000),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Icon(
                        imageVector = vectorResource(id = R.drawable.ic_play),
                        contentDescription = "play",
                        tint = Color.White,
                    )
                }
            }
        }
    }
}

@Composable
private fun loadPicture(uri: Uri): PictureLoadState {
    val context = AmbientContext.current
    val (bitmapState, _) = remember {
        val state = mutableStateOf<PictureLoadState>(PictureLoadState.Loading)
        val (_, setState) = state
        Glide.with(context)
            .asBitmap()
            .load(uri)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    setState(PictureLoadState.Success(resource))
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
        state
    }

    return bitmapState
}

private sealed class PictureLoadState {
    object Loading : PictureLoadState()
    data class Success(val resource: Bitmap) : PictureLoadState()
}

@Composable
private fun VideoRecord(record: RecordUiModel, playerCache: RecordsPlayerCache) {
    Surface {
        val player = remember {
            playerCache.acquire(record)
        }
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f),
            viewBlock = {
                player
            }
        ) {
            if (it.currentPlayId != record.id) {
                it.setRecord(record)
            }
            it.player.prepare()
            it.player.play()
            it.hideController()
        }
    }
}