package ru.falseteam.control.server.domain.videoencoder

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Path

class VideoEncodeInteractorImpl : VideoEncodeInteractor {
    private val log = LoggerFactory.getLogger("control.encoder")
    private val executor: FFmpegExecutor
    private val ffprobe: FFprobe

    init {
        val path = findInPath("ffmpeg")?.parent ?: throw Exception("ffmpeg not found")
        log.info("ffmpeg found in $path")
        val ffmpeg = FFmpeg(path + "ffmpeg")
        ffprobe = FFprobe(path + "ffprobe")
        executor = FFmpegExecutor(ffmpeg, ffprobe)
    }

    override suspend fun encode(input: Path, output: Path) = withContext(Dispatchers.IO) {
        val builder = FFmpegBuilder()
            .setVerbosity(FFmpegBuilder.Verbosity.QUIET)
            .setInput(input.toAbsolutePath().toString())
            .addOutput(output.toAbsolutePath().toString())
            //.setVideoFrameRate(24, 1)
            .addExtraArgs("-c", "copy")
            .done()

        executor.createJob(builder).run()
    }

    override suspend fun getDuration(file: Path): Double = withContext(Dispatchers.IO) {
        ffprobe.probe(file.toAbsolutePath().toString())
            .getFormat().duration
    }


    //TODO move out
    private fun findInPath(name: String): File? {
        return System.getenv("PATH").split(File.pathSeparator)
            .asSequence()
            .map { File(it) }
            .map { it.resolve(name) }
            .filter { it.exists() }
            .firstOrNull()
    }
}
