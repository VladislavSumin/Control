package ru.falseteam.control.server.domain.cams

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.control.camsconnection.AbstractCameraConnection
import ru.falseteam.control.camsconnection.CameraConnection
import ru.falseteam.control.camsconnection.CameraConnectionState
import ru.falseteam.control.camsconnection.CameraVideoConnection

class CamsConnectionInteractorImpl(
    private val camsInteractor: CamsInteractor
) : CamsConnectionInteractor {
    private val log = LoggerFactory.getLogger("controls.cams")

    override suspend fun processConnections() = coroutineScope {
        log.debug("Start process camera connection")
        try {
            camsInteractor.observeAll().collectLatest {
                processCams(it)
            }
        } finally {
            log.debug("Stopped process camera connection")
        }
    }

    private suspend fun processCams(cams: List<CameraDTO>) = coroutineScope {
        try {
            log.debug("Start process cams: ${cams.joinToString { it.name }}")
            cams.forEach { launch { processCamera(it) } }
        } finally {
            log.debug("Stopped process cams")
        }
    }

    private suspend fun processCamera(camera: CameraDTO) {
        try {
            log.trace("Start process camera ${camera.name}")
            CameraConnection(
                address = camera.address,
                port = camera.port
            ).connectionObservable.collectLatest {
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
            log.trace("Stopped process camera ${camera.name}")
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