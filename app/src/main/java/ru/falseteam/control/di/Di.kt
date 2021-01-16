package ru.falseteam.control.di

import androidx.lifecycle.ViewModelProvider
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.websocket.*
import org.kodein.di.*
import ru.falseteam.control.api.CamsApi
import ru.falseteam.control.api.CamsApiImpl
import ru.falseteam.control.api.rsub.CamsRSub
import ru.falseteam.control.api.rsub.CamsRecordRSub
import ru.falseteam.control.api.rsub.CamsStatusRSub
import ru.falseteam.control.domain.cams.CamsInteractor
import ru.falseteam.control.domain.cams.CamsInteractorImpl
import ru.falseteam.control.domain.records.RecordsInteractor
import ru.falseteam.control.domain.records.RecordsInteractorImpl
import ru.falseteam.control.domain.themes.ThemesInteractor
import ru.falseteam.control.domain.themes.ThemesInteractorImpl
import ru.falseteam.control.ui.screens.addcamera.AddCameraViewModel
import ru.falseteam.control.ui.screens.cams.CamsViewModel
import ru.falseteam.control.ui.screens.recors.RecordsViewModel
import ru.falseteam.rsub.client.RSubClient
import ru.falseteam.rsub.connector.ktorwebsocket.client.RSubConnectorKtorWebSocket

val Kodein = DI.lazy {
    // Domain
    bind<CamsInteractor>() with singleton { CamsInteractorImpl(instance(), instance(), instance()) }
    bind<RecordsInteractor>() with singleton { RecordsInteractorImpl(instance()) }
    bind<ThemesInteractor>() with singleton { ThemesInteractorImpl() }

    // Api
    bind<CamsApi>() with singleton { CamsApiImpl(instance()) }

    // ViewModel
    bind<ViewModelProvider.Factory>() with singleton { DiViewModelFactory(directDI) }
    bindViewModel<AddCameraViewModel>() with provider { AddCameraViewModel(instance()) }
    bindViewModel<CamsViewModel>() with provider { CamsViewModel(instance()) }
    bindViewModel<RecordsViewModel>() with provider { RecordsViewModel(instance()) }

    // rSub
    bind<HttpClient>() with singleton {
        HttpClient {
            install(JsonFeature)
            install(WebSockets)
        }
    }
    bind<RSubClient>() with singleton {
        RSubClient(
            RSubConnectorKtorWebSocket(
                instance(),
                host = "10.0.0.56"
            )
        )
    }
    bind<CamsRSub>() with singleton { instance<RSubClient>().getProxy(CamsRSub::class) }
    bind<CamsStatusRSub>() with singleton { instance<RSubClient>().getProxy(CamsStatusRSub::class) }
    bind<CamsRecordRSub>() with singleton { instance<RSubClient>().getProxy(CamsRecordRSub::class) }
}
