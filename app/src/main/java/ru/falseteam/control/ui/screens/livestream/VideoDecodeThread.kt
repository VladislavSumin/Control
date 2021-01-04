package ru.falseteam.control.ui.screens.livestream

import android.content.res.AssetFileDescriptor
import android.media.MediaCodec
import android.media.MediaFormat
import android.util.Log
import android.view.Surface
import kotlinx.coroutines.*
import java.io.FileInputStream
import java.lang.Exception
import java.nio.ByteBuffer
import kotlin.experimental.and


class VideoDecodeThread : Thread() {

    companion object {
        private const val TAG = "VideoDecoder"
    }

    private lateinit var decoder: MediaCodec
    private lateinit var inputArray: ByteArray
    private lateinit var surface: Surface
    var rootIndex = 0

    private var isFirst = false
    private var startWhen = 0L
    private var time = 0L
    private val newBufferInfo = MediaCodec.BufferInfo()


    private var isStop = false

    fun init(surface: Surface, file: AssetFileDescriptor): Boolean {
        this.surface = surface

        val size = file.length
        inputArray = ByteArray(size.toInt())
        val inputStream = file.createInputStream()
        val read = inputStream.read(inputArray)

        if (read.toLong() != size) throw Exception()

        isStop = false
        decoder = MediaCodec.createDecoderByType("video/avc")
        return true
    }

    override fun run() {
        val csd = ByteBuffer.allocate(200)
        val csdSize = readNALU(csd)
//
        val csd2 = ByteBuffer.allocate(200)
        val csdSize2 = readNALU(csd)

        val format = MediaFormat.createVideoFormat("video/avc", 0, 0)
        format.setByteBuffer("csd-0", ByteBuffer.wrap(csd.array(), 0, csdSize))
        format.setByteBuffer("csd-1", ByteBuffer.wrap(csd2.array(), 0, csdSize2))

        decoder.configure(format, surface, null, 0 /* Decode */)
        decoder.start()


        val inputBuffers: Array<ByteBuffer> = decoder.inputBuffers


        val job = GlobalScope.launch(Dispatchers.IO) {
            while (!isStop) {
                decoder.dequeueInputBuffer(1000).takeIf { it >= 0 }?.let { index ->
                    val inputBuffer = inputBuffers[index]

                    val sampleSize = readNALU(inputBuffer)
                    val naluTypeRaw = inputBuffer.get(3)
                    val naluType = naluTypeRaw and 0b000011111
                    time += if (naluType > 5) 0 else 66000
                    decoder.queueInputBuffer(index, 0, sampleSize, time, 0)
                }
            }
        }

        while (!isStop) {
            readDecodedFrame()
        }

        runBlocking {
            job.cancelAndJoin()
        }

        decoder.stop()
        decoder.release()
    }

    private fun readDecodedFrame() {
        when (val outIndex = decoder.dequeueOutputBuffer(newBufferInfo, 1000)) {
            MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED -> {
                Log.d(TAG, "INFO_OUTPUT_BUFFERS_CHANGED")
                decoder.outputBuffers
            }
            MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                Log.d(TAG, "INFO_OUTPUT_FORMAT_CHANGED format : " + decoder.outputFormat)
            }
            MediaCodec.INFO_TRY_AGAIN_LATER -> {
                Log.d(TAG, "INFO_TRY_AGAIN_LATER")
            }
            else -> {
                if (!isFirst) {
                    startWhen = System.currentTimeMillis()
                    isFirst = true
                }
                try {
                    val sleepTime: Long =
                        newBufferInfo.presentationTimeUs / 1000 - (System.currentTimeMillis() - startWhen)
                    Log.d(
                        TAG,
                        "info.presentationTimeUs : " + (newBufferInfo.presentationTimeUs).toString() + " playTime: " + (System.currentTimeMillis() - startWhen).toString() + " sleepTime : " + sleepTime
                    )
                    if (sleepTime > 0) sleep(sleepTime)

                } catch (e: InterruptedException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }

                decoder.releaseOutputBuffer(outIndex, true /* Surface init */)
            }
        }

//         All decoded frames have been rendered, we can stop playing now
//        if (newBufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
//            Log.d(TAG, "OutputBuffer BUFFER_FLAG_END_OF_STREAM")
//            break
//        }
    }

    private fun readNALU(byteBuffer: ByteBuffer): Int {
        val startIndex = rootIndex
        rootIndex += 3
        while (true) {
            if (inputArray[rootIndex] == 0.toByte()
                && inputArray[rootIndex + 1] == 0.toByte()
                && inputArray[rootIndex + 2] == 1.toByte()
            ) {
                break
            }
            rootIndex++
        }
        val size = rootIndex - startIndex
        byteBuffer.put(inputArray, startIndex, size)
        return size
    }

    fun close() {
        isStop = true
    }
}