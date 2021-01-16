package ru.falseteam.control.ui.screens.recors

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.util.Pools
import androidx.navigation.NavController
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import ru.falseteam.control.R
import ru.falseteam.control.api.dto.CameraRecordDTO
import ru.falseteam.control.di.kodeinViewModel
import ru.falseteam.control.ui.red900

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
private fun RecordsList(records: List<RecordUiModel>) {
    val context = AmbientContext.current
    val playerCache = remember {
        PlayerCache(context)
    }

    LazyColumn {
        items(records) { record ->
            RecordCard(record = record, playerCache)
        }
    }
}

@Composable
private fun RecordCard(record: RecordUiModel, playerCache: PlayerCache) {
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
                    text = "Без названия...",
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(16.dp, 0.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(vectorResource(id = R.drawable.ic_edit))
                }
            }
            VideoRecord(record.id, playerCache)
            Text(text = record.id.toString())
            Divider(modifier = Modifier.padding(8.dp, 0.dp))
            Row {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(vectorResource(id = R.drawable.ic_save))

                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(vectorResource(id = R.drawable.ic_delete), tint = red900)
                }
            }
        }
    }
}

@Composable
private fun VideoRecord(id: Long, playerCache: PlayerCache) {
    Surface {
        val (oldId, setOldId) = remember { mutableStateOf(-1L) }
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
            if (oldId != id) {
                setOldId(id)
                it.player?.apply {
                    this as SimpleExoPlayer
                    setMediaSource(playerCache.createMedia(id))
                    prepare()
                }
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

    fun createMedia(id: Long): ProgressiveMediaSource {
        val uri = Uri.parse("http://10.0.0.56:8080/api/v1/records/video/$id")
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
    attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : PlayerView(context, attrs, defStyleAttr) {
    var cache: PlayerCache? = null

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        player?.stop()
        cache?.release(this)
    }
}