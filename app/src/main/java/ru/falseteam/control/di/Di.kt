package ru.falseteam.control.di

import androidx.lifecycle.ViewModelProvider
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.websocket.*
import kotlinx.coroutines.runBlocking
import org.kodein.di.*
import org.kodein.di.android.x.androidXModule
import ru.falseteam.control.App
import ru.falseteam.control.api.CamsApi
import ru.falseteam.control.api.CamsApiImpl
import ru.falseteam.control.api.RecordsApi
import ru.falseteam.control.api.RecordsApiImpl
import ru.falseteam.control.api.rsub.CamsRSub
import ru.falseteam.control.api.rsub.CamsRecordRSub
import ru.falseteam.control.api.rsub.EntitiesRSub
import ru.falseteam.control.domain.analytic.AnalyticInteractor
import ru.falseteam.control.domain.analytic.AnalyticInteractorImpl
import ru.falseteam.control.domain.cams.CamsInteractor
import ru.falseteam.control.domain.cams.CamsInteractorImpl
import ru.falseteam.control.domain.entities.EntitiesInteractor
import ru.falseteam.control.domain.entities.EntitiesInteractorImpl
import ru.falseteam.control.domain.records.RecordsInteractor
import ru.falseteam.control.domain.records.RecordsInteractorImpl
import ru.falseteam.control.domain.servers.ServersInteractor
import ru.falseteam.control.domain.servers.ServersInteractorImpl
import ru.falseteam.control.domain.themes.ThemesInteractor
import ru.falseteam.control.domain.themes.ThemesInteractorImpl
import ru.falseteam.control.repository.servers.ServerRepositoryImpl
import ru.falseteam.control.repository.servers.ServersRepository
import ru.falseteam.control.repository.themes.ThemesRepository
import ru.falseteam.control.repository.themes.ThemesRepositoryImpl
import ru.falseteam.control.ui.screens.addcamera.AddCameraViewModel
import ru.falseteam.control.ui.screens.addserver.AddServerViewModel
import ru.falseteam.control.ui.screens.cams.CamsViewModel
import ru.falseteam.control.ui.screens.entities.EntitiesViewModel
import ru.falseteam.control.ui.screens.recors.RecordsViewModel
import ru.falseteam.rsub.client.RSubClient
import ru.falseteam.rsub.connector.ktorwebsocket.client.RSubConnectorKtorWebSocket

val Kodein = DI.lazy {
    import(androidXModule(App.instace))

    // Repository
    bind<ThemesRepository>() with singleton { ThemesRepositoryImpl(instance()) }
    bind<ServersRepository>() with singleton { ServerRepositoryImpl(instance()) }

    // Domain
    bind<AnalyticInteractor>() with singleton { AnalyticInteractorImpl(instance()) }
    bind<CamsInteractor>() with singleton {
        CamsInteractorImpl(instance(), instance(), instance())
    }
    bind<RecordsInteractor>() with singleton {
        RecordsInteractorImpl(instance(), instance(), instance())
    }
    bind<ThemesInteractor>() with singleton { ThemesInteractorImpl(instance()) }
    bind<ServersInteractor>() with singleton { ServersInteractorImpl(instance()) }
    bind<EntitiesInteractor>() with singleton { EntitiesInteractorImpl(instance()) }

    // Api
    bind<CamsApi>() with singleton { CamsApiImpl(instance()) }
    bind<RecordsApi>() with singleton { RecordsApiImpl(instance()) }

    // ViewModel
    bind<ViewModelProvider.Factory>() with singleton { DiViewModelFactory(directDI) }
    bindViewModel<AddCameraViewModel>() with provider { AddCameraViewModel(instance()) }
    bindViewModel<CamsViewModel>() with provider { CamsViewModel(instance()) }
    bindViewModel<RecordsViewModel>() with provider { RecordsViewModel(instance(), instance()) }
    bindViewModel<AddServerViewModel>() with provider { AddServerViewModel(instance()) }
    bindViewModel<EntitiesViewModel>() with provider { EntitiesViewModel(instance()) }

    // rSub
    bind<HttpClient>() with singleton {
        HttpClient {
            install(JsonFeature)
            install(WebSockets)
        }
    }
    bind<RSubClient>() with singleton {
        // TODO rewrite to factory
        runBlocking {
            val server = instance<ServersInteractor>().getPrimaryServer()
            RSubClient(
                RSubConnectorKtorWebSocket(
                    instance(),
                    host = server.host,
                    port = server.port,
                )
            )
        }
    }
    bind<CamsRSub>() with singleton { instance<RSubClient>().getProxy(CamsRSub::class) }
    bind<CamsRecordRSub>() with singleton { instance<RSubClient>().getProxy(CamsRecordRSub::class) }
    bind<EntitiesRSub>() with singleton { instance<RSubClient>().getProxy(EntitiesRSub::class) }
}
