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

class CamsConnectionInteractorImpl(
    private val camsInteractor: CamsInteractor
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
                        it.movementEvent
                            .flatMapLatest {
                                flow {
                                    emit(true)
                                    delay(5000)
                                    emit(false)
                                }
                            }
                            .distinctUntilChanged()
                            .collect { println("MOVEMENT ALARM, URGENT!!!! (state = $it)") }
                    }
//                println("New camera status for ${camera.name} is $it")
                }

        } finally {
            withContext(NonCancellable) {
                statusChannel.sendSuppressClosed(camera.id to null)
                log.trace("Stopped process camera ${camera.name}")
            }
        }
    }

    private suspend fun <T> SendChannel<T>.sendSuppressClosed(element: T) {
        try {
            send(element)
        } catch (e: ClosedSendChannelException) {
            // ignore
        }
    }

//    private suspend fun processCamera(camera: CameraDTO) {
//        try {
//            log.trace("Start process camera ${camera.name}")
//            CameraVideoConnection(
//                address = camera.address,
//                port = camera.port
//            ).connectionObservable.collectLatest {
//                if (it is CameraConnectionState.ConnectedVideo) {
//                    it.videoFlow
//                        .collect {
//                            println("MOVEMENT ALARM, URGENT!!!!")
//                        }
//                }
////                println("New camera status for ${camera.name} is $it")
//            }
//
//        } finally {
//            log.trace("Stopped process camera ${camera.name}")
//        }
//    }
}