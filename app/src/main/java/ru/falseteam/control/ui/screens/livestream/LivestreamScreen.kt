package ru.falseteam.control.ui.screens.livestream

import android.content.res.AssetFileDescriptor
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Button
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.viewinterop.AndroidView
import org.kodein.di.direct
import org.kodein.di.instance
import ru.falseteam.control.R
import ru.falseteam.control.di.Kodein

@Composable
fun LivestreamScreen(id: Long) {
    val context = AmbientContext.current
    val surfaceView = remember {
        SurfaceView(context).apply {
            holder.addCallback(SurfaceCallback(VideoDecodeThread()))
        }
    }

    AndroidView({ surfaceView }) {

    }
}

private class SurfaceCallback(
    private val videoDecode: VideoDecodeThread,
) : SurfaceHolder.Callback {
    override fun surfaceCreated(holder: SurfaceHolder) {
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        videoDecode.init(holder.surface, Kodein.direct.instance())
        videoDecode.start()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        videoDecode.close()
    }

}
