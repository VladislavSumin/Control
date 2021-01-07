package ru.falseteam.control.server.domain.cams

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.*
import org.slf4j.LoggerFactory
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.control.api.dto.CameraStatusDTO
import ru.falseteam.control.camsconnection.CameraConnection
import ru.falseteam.control.camsconnection.CameraConnectionState
import ru.falseteam.control.server.domain.records.RecordsInteractor
import ru.falseteam.control.server.domain.videoencoder.VideoEncodeInteractor
import java.io.File

//TODO зарефачить это г.
class CamsConnectionInteractorImpl(
    private val camsInteractor: CamsInteractor,
    private val videoEncodeInteractor: VideoEncodeInteractor,
    private val recordsInteractor: RecordsInteractor,
) : CamsConnectionInteractor {
    private val log = LoggerFactory.getLogger("controls.cams")

    private val cameraStatusMap: MutableMap<Long, CameraStatusDTO> = HashMap()
    private val cameraStatusState: MutableStateFlow<Map<Long, CameraStatusDTO>> =
        MutableStateFlow(emptyMap())

    private val cameraConnectionsObservable: Flow<Map<CameraDTO, CameraConnection>> =
        camsInteractor.observeAll()
            .map { cams -> cams.associateWith { CameraConnection(it.address, it.port) } }
            .shareIn(GlobalScope, SharingStarted.WhileSubscribed(replayExpirationMillis = 0), 1)

    override suspend fun processConnections() = coroutineScope {
        log.debug("Start process camera connection")
        try {
            cameraConnectionsObservable.collectLatest {
                coroutineScope {
                    val statusChannel = setupStatusChannel(this)
                    processCams(it, statusChannel)
                }
            }
        } finally {
            log.debug("Stopped process camera connection")
        }
    }

    override fun observeStatus(): Flow<Map<Long, CameraStatusDTO>> = cameraStatusState

    override suspend fun observeVideoStream(id: Long): Flow<ByteArray> =
        cameraConnectionsObservable.map { cams ->
            cams.asSequence().first { it.key.id == id }.value
        }.flatMapLatest {
            it.videoObservable
        }

    private suspend fun setupStatusChannel(scope: CoroutineScope): SendChannel<Pair<Long, CameraStatusDTO?>> {
        val channel = Channel<Pair<Long, CameraStatusDTO?>>()
        scope.launch {
            try {
                for (pair in channel) {
                    if (pair.second == null) cameraStatusMap.remove(pair.first)
                    else cameraStatusMap[pair.first] = pair.second!!
                    cameraStatusState.emit(cameraStatusMap.toMap())
                }
            } finally {
                withContext(NonCancellable) {
                    channel.close()
                    cameraStatusMap.clear()
                    cameraStatusState.emit(emptyMap())
                }
            }
        }
        return channel
    }

    private suspend fun processCams(
        cams: Map<CameraDTO, CameraConnection>,
        statusChannel: SendChannel<Pair<Long, CameraStatusDTO?>>
    ) = coroutineScope {
        try {
            log.debug("Start process cams: ${cams.keys.joinToString { it.name }}")
            cams.forEach { launch { processCamera(it.key, it.value, statusChannel) } }
        } finally {
            log.debug("Stopped process cams")
        }
    }

    private suspend fun processCamera(
        camera: CameraDTO,
        cameraConnection: CameraConnection,
        statusChannel: SendChannel<Pair<Long, CameraStatusDTO?>>
    ) {
        try {
            log.trace("Start process camera ${camera.name}")
            cameraConnection.connectionObservable
                .onEach {
                    val status = when (it) {
                        is CameraConnectionState.Connecting -> CameraStatusDTO.Connecting
                        is CameraConnectionState.Connected -> CameraStatusDTO.Connected
                        is CameraConnectionState.Disconnected -> CameraStatusDTO.Disconnected
                        else -> throw Exception("Unknown connection state: $it")
                    }
                    statusChannel.send(camera.id to status)
                }
                .collectLatest {
                    if (it is CameraConnectionState.Connected) {
                        processMovement(camera, cameraConnection, it)
                    }
                }

        } finally {
            withContext(NonCancellable) {
                statusChannel.sendSuppressClosed(camera.id to null)
                log.trace("Stopped process camera ${camera.name}")
            }
        }
    }

    private suspend fun processMovement(
        camera: CameraDTO,
        cameraConnection: CameraConnection,
        connectionState: CameraConnectionState.Connected
    ) {
        connectionState.movementEvent
            .flatMapLatest {
                flow {
                    emit(true)
                    delay(8000)
                    emit(false)
                }
            }
            .distinctUntilChanged()
            .collectLatest {
                if (it) {
                    recording(camera, cameraConnection)
                }
            }
    }

    private suspend fun recording(
        camera: CameraDTO,
        cameraConnection: CameraConnection,
    ) {
        log.debug("Start recording from ${camera.name}")
        val currentTimeMillis = System.currentTimeMillis()
        val name = "${camera.id}_$currentTimeMillis"
        val file = File("data/tmp/$name.h264")
        file.parentFile.mkdirs()
        file.createNewFile()
        val stream = file.outputStream()
        try {
            withContext(Dispatchers.IO) {
                cameraConnection.videoObservable.collect { stream.write(it) }
            }
        } finally {
            withContext(NonCancellable) {
                stream.close()
                log.debug("Finish recording from ${camera.name}")
                transcodeAndSave(file, camera, name, currentTimeMillis)
            }
        }
    }

    private fun transcodeAndSave(file: File, camera: CameraDTO, name: String, timestamp: Long) {
        GlobalScope.launch(Dispatchers.IO) {
            val output = file.parentFile.resolve("$name.mp4").toPath()
            videoEncodeInteractor.encode(
                file.toPath(),
                output
            )
            file.delete()
            recordsInteractor.saveNewRecord(camera, timestamp, output)
        }
    }

    private suspend fun <T> SendChannel<T>.sendSuppressClosed(element: T) {
        try {
            send(element)
        } catch (e: ClosedSendChannelException) {
            // ignore
        }
    }
}