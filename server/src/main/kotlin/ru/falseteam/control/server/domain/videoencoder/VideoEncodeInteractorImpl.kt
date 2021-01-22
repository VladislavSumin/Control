package ru.falseteam.control.server.domain.videoencoder

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import org.slf4j.LoggerFactory
import ru.falseteam.control.server.utils.PathUtils
import java.nio.file.Path

class VideoEncodeInteractorImpl : VideoEncodeInteractor {
    private val log = LoggerFactory.getLogger("control.encoder")
    private val executor: FFmpegExecutor
    private val ffprobe: FFprobe

    init {
        val ffmpeg = FFmpeg(PathUtils.getPathOfProgram("ffmpeg")
            ?: throw Exception("ffmpeg not found"))
        ffprobe = FFprobe(PathUtils.getPathOfProgram("ffprobe")
            ?: throw Exception("ffprobe not found"))
        executor = FFmpegExecutor(ffmpeg, ffprobe)
    }

    override suspend fun encode(input: Path, output: Path) = withContext(Dispatchers.IO) {
        val builder = FFmpegBuilder()
            .setVerbosity(FFmpegBuilder.Verbosity.QUIET)
            .setInput(input.toAbsolutePath().toString())
            .addOutput(output.toAbsolutePath().toString())
            // .setVideoFrameRate(24, 1)
            .addExtraArgs("-c", "copy")
            .done()

        executor.createJob(builder).run()
    }

    override suspend fun getDuration(file: Path): Double = withContext(Dispatchers.IO) {
        ffprobe.probe(file.toAbsolutePath().toString())
            .getFormat().duration
    }
}
