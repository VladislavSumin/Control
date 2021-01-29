package ru.falseteam.control.server.domain.cams

import io.ktor.network.selector.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.slf4j.LoggerFactory
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.control.api.dto.CameraStatusDTO
import ru.falseteam.control.camsconnection.CameraConnection
import ru.falseteam.control.camsconnection.CameraConnectionState
import ru.falseteam.control.camsconnection.CameraConnectionStatus
import ru.falseteam.control.server.domain.records.RecordsInteractor
import ru.falseteam.control.server.domain.videoencoder.VideoEncodeInteractor
import java.io.File

// TODO зарефачить это г.
class CamsConnectionInteractorImpl(
    camsInteractor: CamsInteractor,
    private val videoEncodeInteractor: VideoEncodeInteractor,
    private val recordsInteractor: RecordsInteractor,
) : CamsConnectionInteractor {
    private val log = LoggerFactory.getLogger("controls.cams")
    private val selectorManager: SelectorManager = ActorSelectorManager(Dispatchers.IO)

    private val cameraConnectionsObservable: Flow<Map<CameraDTO, CameraConnection>> =
        camsInteractor.observeAll()
            .map { cams ->
                cams.associateWith {
                    CameraConnection(
                        selectorManager,
                        it.address,
                        it.port
                    )
                }
            }
            .shareIn(GlobalScope, SharingStarted.WhileSubscribed(replayExpirationMillis = 0), 1)

    private val cameraStatusObservable =
        cameraConnectionsObservable.flatMapLatest { cams ->
            channelFlow {
                cams.forEach { (camera, cameraConnection) ->
                    launch {
                        cameraConnection.observeConnectionStatus().collect {
                            val state = when (it) {
                                // TODO fix states
                                CameraConnectionStatus.CONNECTED -> CameraStatusDTO.Connected
                                CameraConnectionStatus.CONNECTING -> CameraStatusDTO.Connecting
                                CameraConnectionStatus.DISCONNECTED,
                                CameraConnectionStatus.ERROR -> CameraStatusDTO.Disconnected
                            }
                            send(camera.id to state)
                        }
                    }
                }
            }
                .scan(mutableMapOf<Long, CameraStatusDTO>()) { list, (id, state) ->
                    list[id] = state
                    list.toMutableMap()
                }
        }
            .shareIn(GlobalScope, SharingStarted.WhileSubscribed(replayExpirationMillis = 0), 1)

    override suspend fun processConnections() = coroutineScope {
        log.debug("Start process camera connection")
        try {
            cameraConnectionsObservable.collectLatest {
                coroutineScope {
                    processCams(it)
                }
            }
        } finally {
            log.debug("Stopped process camera connection")
        }
    }

    override fun observeStatus(): Flow<Map<Long, CameraStatusDTO>> = cameraStatusObservable

    override suspend fun observeVideoStream(id: Long): Flow<ByteArray> =
        cameraConnectionsObservable.map { cams ->
            cams.asSequence().first { it.key.id == id }.value
        }.flatMapLatest {
            it.videoObservable
        }

    private suspend fun processCams(
        cams: Map<CameraDTO, CameraConnection>,
    ) = coroutineScope {
        try {
            log.debug("Start process cams: ${cams.keys.joinToString { it.name }}")
            cams.forEach { launch { processCamera(it.key, it.value) } }
        } finally {
            log.debug("Stopped process cams")
        }
    }

    private suspend fun processCamera(
        camera: CameraDTO,
        cameraConnection: CameraConnection,
    ) {
        try {
            log.trace("Start process camera ${camera.name}")
            cameraConnection.connectionObservable
                .collectLatest {
                    if (it is CameraConnectionState.Connected) {
                        processMovement(camera, cameraConnection, it)
                    }
                }
        } finally {
            withContext(NonCancellable) {
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
        val file = recordsInteractor.getRecordsTmpPath().resolve("$name.h264").toFile()
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
}