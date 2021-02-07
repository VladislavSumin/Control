package ru.falseteam.control.server.di

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.kodein.di.*
import ru.falseteam.control.api.rsub.CamsRSub
import ru.falseteam.control.api.rsub.CamsRecordRSub
import ru.falseteam.control.server.api.CamsApi
import ru.falseteam.control.server.api.RecordsApi
import ru.falseteam.control.server.database.CameraQueries
import ru.falseteam.control.server.database.CameraRecordQueries
import ru.falseteam.control.server.database.Database
import ru.falseteam.control.server.domain.cams.CamsConnectionInteractor
import ru.falseteam.control.server.domain.cams.CamsConnectionInteractorImpl
import ru.falseteam.control.server.domain.cams.CamsInteractor
import ru.falseteam.control.server.domain.cams.CamsInteractorImpl
import ru.falseteam.control.server.domain.configuration.ConfigurationInteractor
import ru.falseteam.control.server.domain.configuration.ConfigurationInteractorImpl
import ru.falseteam.control.server.domain.debug.DebugInteractor
import ru.falseteam.control.server.domain.debug.DebugInteractorImpl
import ru.falseteam.control.server.domain.records.RecordsInteractor
import ru.falseteam.control.server.domain.records.RecordsInteractorImpl
import ru.falseteam.control.server.domain.videoencoder.VideoEncodeInteractor
import ru.falseteam.control.server.domain.videoencoder.VideoEncodeInteractorImpl
import ru.falseteam.control.server.repository.CameraRecordsRepository
import ru.falseteam.control.server.repository.CameraRecordsRepositoryImpl
import ru.falseteam.control.server.repository.ServerConfigurationRepository
import ru.falseteam.control.server.repository.ServerConfigurationRepositoryImpl
import ru.falseteam.control.server.rsub.CamsRSubImpl
import ru.falseteam.control.server.rsub.CamsRecordRSubImpl
import ru.falseteam.rsub.server.RSubServer
import java.nio.file.Path

val Kodein = DI {
    // Repository
    bind<ServerConfigurationRepository>() with singleton { ServerConfigurationRepositoryImpl() }
    bind<CameraRecordsRepository>() with singleton { CameraRecordsRepositoryImpl(instance()) }

    // Database
    bind<SqlDriver>() with singleton { JdbcSqliteDriver("jdbc:sqlite:data/database.sqlite") }
    bind<Database>() with singleton {
        Path.of("data").toFile().mkdirs()
        if (!Path.of("data/database.sqlite").toFile().exists()) {
            Database.Schema.create(instance())
        }
        Database(instance())
    }
    bind<CameraQueries>() with factory { instance<Database>().cameraQueries }
    bind<CameraRecordQueries>() with factory { instance<Database>().cameraRecordQueries }

    // Domain
    bind<CamsInteractor>() with singleton { CamsInteractorImpl(instance()) }
    bind<CamsConnectionInteractor>() with singleton {
        CamsConnectionInteractorImpl(instance(), instance(), instance())
    }
    bind<RecordsInteractor>() with singleton {
        RecordsInteractorImpl(instance(), instance(), instance())
    }
    bind<VideoEncodeInteractor>() with singleton { VideoEncodeInteractorImpl() }
    bind<ConfigurationInteractor>() with singleton { ConfigurationInteractorImpl(instance()) }
    bind<DebugInteractor>() with singleton { DebugInteractorImpl(instance()) }

    // Api
    bind<CamsApi>() with singleton { CamsApi(instance(), instance()) }
    bind<RecordsApi>() with singleton { RecordsApi(instance()) }

    // rSub
    bind<RSubServer>() with singleton {
        RSubServer().apply {
            registerImpl(instance<CamsRSub>())
            registerImpl(instance<CamsRecordRSub>())
        }
    }
    bind<CamsRSub>() with singleton { CamsRSubImpl(instance(), instance()) }
    bind<CamsRecordRSub>() with singleton { CamsRecordRSubImpl(instance()) }
}
