package ru.falseteam.control.ui.screens.livestream

import android.content.res.AssetFileDescriptor
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.viewinterop.AndroidView
import ru.falseteam.control.R

@Composable
fun LivestreamScreen() {
    val context = AmbientContext.current
    val videoPath = context.resources.openRawResourceFd(R.raw.test2)
    val surfaceView = remember {
        SurfaceView(context).apply {
            holder.addCallback(SurfaceCallback(VideoDecodeThread(), videoPath))
        }
    }

    AndroidView({ surfaceView }) {

    }
}

private class SurfaceCallback(
    private val videoDecode: VideoDecodeThread,
    private val videoPath: AssetFileDescriptor
) : SurfaceHolder.Callback {
    override fun surfaceCreated(holder: SurfaceHolder) {
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        videoDecode.init(holder.surface, videoPath)
        videoDecode.start()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        videoDecode.close()
    }

}
