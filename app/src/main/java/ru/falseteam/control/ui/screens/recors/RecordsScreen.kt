package ru.falseteam.control.ui.screens.recors

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.widget.CalendarView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.util.Pools
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.runBlocking
import ru.falseteam.control.R
import ru.falseteam.control.di.kodeinViewModel
import ru.falseteam.uikit.elements.UiKitPrimaryButton
import ru.falseteam.uikit.elements.UikitCheckBoxListItem
import ru.falseteam.uikit.red900
import java.time.LocalDate

@Composable
fun RecordsScreen(viewModel: RecordsViewModel = kodeinViewModel()) {
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(scaffoldState, viewModel) },
        drawerContent = { FilterContent(viewModel) },
    ) { Content(viewModel) }
}

@Composable
private fun Content(viewModel: RecordsViewModel) {
    ChangeNameScreen(viewModel)

    when (val state = viewModel.state.collectAsState().value) {
        RecordsState.Loading -> LoadingScreen()
        is RecordsState.Error -> ErrorScreen(state, viewModel)
        is RecordsState.ShowResult -> ShowResultScreen(state, viewModel)
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
private fun FilterContent(viewModel: RecordsViewModel) {
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
private fun LoadingScreen() {
    // TODO make global function like this
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
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
private fun ShowResultScreen(state: RecordsState.ShowResult, viewModel: RecordsViewModel) {
    RecordsList(state.records, viewModel)
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

                //TODO add animation
                if (state is RecordRenameDialogState.Error) {
                    Text(
                        text = "Error: ${state.exception.message}",
                        color = red900
                    ) //TODO use theme
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
private fun RecordsList(records: List<RecordUiModel>, viewModel: RecordsViewModel) {
    val context = AmbientContext.current
    val playerCache = remember {
        PlayerCache(context)
    }

    LazyColumn {
        items(records) { record ->
            RecordCard(record = record, playerCache, viewModel)
        }
    }
}

@Composable
private fun RecordCard(
    record: RecordUiModel,
    playerCache: PlayerCache,
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

            VideoRecord(record.uri, playerCache)

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
private fun VideoRecord(uri: Uri, playerCache: PlayerCache) {
    Surface {
        val player = remember {
            playerCache.acquire()
        }
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f),
            viewBlock = {
                player
            }) {
            it.player?.apply {
                this as SimpleExoPlayer
                setMediaSource(playerCache.createMedia(uri))
                prepare()
            }
        }
    }
}

private class PlayerCache(
    private val context: Context
) {
    private val pool = Pools.SimplePool<MyPlayer>(10)
    private val mediaFactory by lazy {
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, context.packageName)
        )

        ProgressiveMediaSource.Factory(dataSourceFactory)
    }

    fun createMedia(uri: Uri): ProgressiveMediaSource {
        val mediaItem = MediaItem.Builder().setUri(uri).build()
        return mediaFactory.createMediaSource(mediaItem)
    }

    fun acquire(): MyPlayer = pool.acquire() ?: MyPlayer(context).apply {
        player = SimpleExoPlayer.Builder(context).build()
    }

    fun release(player: MyPlayer) = pool.release(player)
}

private class MyPlayer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : PlayerView(context, attrs, defStyleAttr) {
    var cache: PlayerCache? = null

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        player?.stop()
        cache?.release(this)
    }
}