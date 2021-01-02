package ru.falseteam.control.di

import androidx.lifecycle.ViewModelProvider
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.websocket.*
import org.kodein.di.*
import ru.falseteam.control.api.CamsApi
import ru.falseteam.control.api.CamsApiImpl
import ru.falseteam.control.api.rsub.CamsRSub
import ru.falseteam.control.domain.cams.CamsInteractor
import ru.falseteam.control.domain.cams.CamsInteractorImpl
import ru.falseteam.control.ui.screens.AddCameraViewModel
import ru.falseteam.rsub.client.RSubClient
import ru.falseteam.rsub.connector.ktorwebsocket.client.RSubConnectorKtorWebSocket

val Kodein = DI.lazy {
    // Domain
    bind<CamsInteractor>() with singleton { CamsInteractorImpl(instance(), instance()) }

    // Api
    bind<CamsApi>() with singleton { CamsApiImpl(instance()) }

    // ViewModel
    bind<ViewModelProvider.Factory>() with singleton { DiViewModelFactory(directDI) }
    bindViewModel<AddCameraViewModel>() with provider { AddCameraViewModel(instance()) }

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
}
