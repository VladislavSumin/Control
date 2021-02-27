package ru.falseteam.control.ui.screens.livestream

import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.kodein.di.direct
import org.kodein.di.instance
import ru.falseteam.control.di.Kodein

@Composable
fun LivestreamScreen(id: Long) {
    Surface {
        val context = LocalContext.current
        val surfaceView = remember {
            SurfaceView(context).apply {
                holder.addCallback(SurfaceCallback(id, VideoDecodeThread()))
            }
        }

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(1.dp),
            factory = { surfaceView }
        ) {
        }
    }
}

private class SurfaceCallback(
    private val id: Long,
    private val videoDecode: VideoDecodeThread,
) : SurfaceHolder.Callback {
    override fun surfaceCreated(holder: SurfaceHolder) {
        // no action
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        videoDecode.init(holder.surface, Kodein.direct.instance())
        videoDecode.start(id)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        videoDecode.close()
    }
}
