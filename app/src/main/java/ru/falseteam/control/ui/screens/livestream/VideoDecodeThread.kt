package ru.falseteam.control.ui.screens.livestream

import android.content.res.AssetFileDescriptor
import android.media.MediaCodec
import android.media.MediaFormat
import android.util.Log
import android.view.Surface
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.control.domain.cams.CamsInteractor
import java.io.FileInputStream
import java.lang.Exception
import java.nio.ByteBuffer
import kotlin.experimental.and


class VideoDecodeThread : Thread() {

    companion object {
        private const val TAG = "VideoDecoder"
    }

    private lateinit var decoder: MediaCodec
    private lateinit var camsInteractor: CamsInteractor
    private lateinit var surface: Surface
    var rootIndex = 0

    private var isFirst = false
    private var startWhen = 0L
    private var time = 0L
    private val newBufferInfo = MediaCodec.BufferInfo()


    private var isStop = false

    fun init(surface: Surface, camsInteractor: CamsInteractor): Boolean {
        this.surface = surface
        this.camsInteractor = camsInteractor
        isStop = false
        decoder = MediaCodec.createDecoderByType("video/avc")
        return true
    }

    override fun run() {
//        val csd = ByteBuffer.allocate(200)
//        val csdSize = readNALU(csd)
//
//        val csd2 = ByteBuffer.allocate(200)
//        val csdSize2 = readNALU(csd)

        val format = MediaFormat.createVideoFormat("video/avc", 0, 0)
//        format.setByteBuffer("csd-0", ByteBuffer.wrap(csd.array(), 0, csdSize))
//        format.setByteBuffer("csd-1", ByteBuffer.wrap(csd2.array(), 0, csdSize2))

        decoder.configure(format, surface, null, 0 /* Decode */)
        decoder.start()

//        val job = GlobalScope.launch(Dispatchers.IO) {
//            while (!isStop) {
//                decoder.dequeueInputBuffer(1000).takeIf { it >= 0 }?.let { index ->
//                    val inputBuffer = decoder.getInputBuffer(index)!!
//                    val sampleSize = readNALU(inputBuffer)
//                    val naluTypeRaw = inputBuffer.get(3)
//                    val naluType = naluTypeRaw and 0b000011111
//                    time += if (naluType > 5) 0 else 66000
//                    decoder.queueInputBuffer(index, 0, sampleSize, time, 0)
//                }
//            }
//        }

        DataMapper().start()


        readDecodedFrame()

//        runBlocking {
//            job.cancelAndJoin()
//        }

        decoder.stop()
        decoder.release()
    }

    private fun readDecodedFrame() {
        while (!isStop) {
            val index = decoder.dequeueOutputBuffer(newBufferInfo, 1000)
            when {
                index == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED -> {
                    Log.d(TAG, "INFO_OUTPUT_BUFFERS_CHANGED")
                    decoder.outputBuffers
                }
                index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                    Log.d(TAG, "INFO_OUTPUT_FORMAT_CHANGED format : " + decoder.outputFormat)
                }
                index == MediaCodec.INFO_TRY_AGAIN_LATER -> {
                    Log.d(TAG, "INFO_TRY_AGAIN_LATER")
                }
                index >= 0 -> {
                    if (!isFirst) {
                        startWhen = System.currentTimeMillis()
                        isFirst = true
                    }
                    val sleepTime: Long =
                        newBufferInfo.presentationTimeUs / 1000 - (System.currentTimeMillis() - startWhen)
                    if (sleepTime > 0) sleep(sleepTime)
                    decoder.releaseOutputBuffer(index, true /* Surface init */)
                }
                else -> {
                    Log.d(TAG, "UNKNOWN_INDEX")

                }
            }

            if (newBufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                Log.d(TAG, "OutputBuffer BUFFER_FLAG_END_OF_STREAM")
                break
            }
        }
    }

    fun close() {
        isStop = true
    }

    private inner class DataMapper() {
        var bufferIndex = -1
        var receiverBuffer: ByteBuffer = ByteBuffer.allocate(0)
        var emitterIndex = 0
        var zerosCount = 0
        var firstNaluFind = false
        var naluFind = false

        fun start() {
            GlobalScope.launch {
                getBuffer()
                camsInteractor.observeVideoStream(CameraDTO(1, "1", "1", 1)).collect {
                    emitterIndex = 0
                    if (!firstNaluFind) findFirstNalu(it)
                    processInput(it)
                }
            }
        }

        private fun findFirstNalu(input: ByteArray) {
            while (input.size > emitterIndex) {
                val currentByte = input[emitterIndex]
                if (currentByte == 0x00.toByte()) zerosCount++
                if (currentByte == 0x01.toByte() && zerosCount >= 2) {
                    zerosCount = 0
                    emitterIndex++
                    firstNaluFind = true
                    break
                }
                emitterIndex++
            }
        }

        private fun processInput(input: ByteArray) {

            while (input.size > emitterIndex) {
                val currentByte = input[emitterIndex]
                receiverBuffer.put(currentByte)
                if (naluFind) {
                    decoder.queueInputBuffer(
                        bufferIndex,
                        0,
                        receiverBuffer.position() - zerosCount - 2,
                        0,//TODO
                        0
                    )
                    naluFind = false
                    zerosCount = 0
                    getBuffer()
                    receiverBuffer.put(currentByte)
                } else if (currentByte == 0x00.toByte()) zerosCount++
                else if (currentByte == 0x01.toByte() && zerosCount >= 2) naluFind = true
                else zerosCount = 0
                emitterIndex++
            }
        }

        private fun getBuffer() {
            bufferIndex = -1
            while (bufferIndex < 0) {
                if (isStop) throw Exception("stopped")
                bufferIndex = decoder.dequeueInputBuffer(1000)
                if (bufferIndex >= 0) {
                    receiverBuffer = decoder.getInputBuffer(bufferIndex)!!
                    receiverBuffer.position(0)
                    receiverBuffer.put(0x00)
                    receiverBuffer.put(0x00)
                    receiverBuffer.put(0x01)
                }
            }
        }
    }
}