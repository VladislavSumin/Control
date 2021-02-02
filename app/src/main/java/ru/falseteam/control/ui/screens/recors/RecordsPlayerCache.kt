package ru.falseteam.control.ui.screens.recors

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import java.util.*

class RecordsPlayerCache(private val context: Context) {
    private val cache = LinkedList<CacheablePlayerView>()
    private val mediaFactory = MediaFactory(context)

    init {
        repeat(4) {
            release(create())
        }
    }

    fun acquire(record: RecordUiModel): CacheablePlayerView {
        val iterator = cache.iterator()
        while (iterator.hasNext()) {
            val player = iterator.next()
            if (player.currentPlayId == record.id) {
                iterator.remove()
                return player
            }
        }
        return (cache.pollLast() ?: create()).apply { setRecord(record) }
    }

    fun release(view: CacheablePlayerView) {
        cache.addFirst(view)
    }

    private fun create(): CacheablePlayerView {
        return CacheablePlayerView(context, this, mediaFactory)
    }
}

@SuppressLint("ViewConstructor")
class CacheablePlayerView constructor(
    context: Context,
    private val cache: RecordsPlayerCache,
    private val mediaFactory: MediaFactory,
) : PlayerView(context) {
    var currentPlayId: Long = 0
        private set

    init {
        player = SimpleExoPlayer.Builder(context).build()
    }

    fun setRecord(record: RecordUiModel) {
        currentPlayId = record.id
        player.setMediaSource(mediaFactory.createMedia(record.uri))
        player.prepare()
    }


    override fun getPlayer(): SimpleExoPlayer {
        return super.getPlayer() as SimpleExoPlayer
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        player.stop()
        cache.release(this)
    }
}


//TODO make private
class MediaFactory(context: Context) {
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
}

