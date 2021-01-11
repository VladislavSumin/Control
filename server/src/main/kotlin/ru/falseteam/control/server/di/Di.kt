package ru.falseteam.control.server.di

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.kodein.di.*
import ru.falseteam.control.api.rsub.CamsRSub
import ru.falseteam.control.api.rsub.CamsRecordRSub
import ru.falseteam.control.api.rsub.CamsStatusRSub
import ru.falseteam.control.server.api.CamsApi
import ru.falseteam.control.server.api.CamsApiImpl
import ru.falseteam.control.server.database.CameraQueries
import ru.falseteam.control.server.database.CameraRecordQueries
import ru.falseteam.control.server.database.Database
import ru.falseteam.control.server.domain.cams.CamsConnectionInteractor
import ru.falseteam.control.server.domain.cams.CamsConnectionInteractorImpl
import ru.falseteam.control.server.domain.cams.CamsInteractor
import ru.falseteam.control.server.domain.cams.CamsInteractorImpl
import ru.falseteam.control.server.domain.records.RecordsInteractor
import ru.falseteam.control.server.domain.records.RecordsInteractorImpl
import ru.falseteam.control.server.domain.videoencoder.VideoEncodeInteractor
import ru.falseteam.control.server.domain.videoencoder.VideoEncodeInteractorImpl
import ru.falseteam.control.server.rsub.CamsRSubImpl
import ru.falseteam.control.server.rsub.CamsRecordRSubImpl
import ru.falseteam.control.server.rsub.CamsStatusRSubImpl
import ru.falseteam.rsub.server.RSubServer
import java.nio.file.Path

val Kodein = DI {
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
    bind<RecordsInteractor>() with singleton { RecordsInteractorImpl(instance(), instance()) }
    bind<VideoEncodeInteractor>() with singleton { VideoEncodeInteractorImpl() }

    // Api
    bind<CamsApi>() with singleton { CamsApiImpl(instance(), instance()) }

    // rSub
    bind<RSubServer>() with singleton {
        RSubServer().apply {
            registerImpl(instance<CamsRSub>())
            registerImpl(instance<CamsStatusRSub>())
            registerImpl(instance<CamsRecordRSub>())
        }
    }
    bind<CamsRSub>() with singleton { CamsRSubImpl(instance()) }
    bind<CamsStatusRSub>() with singleton { CamsStatusRSubImpl(instance()) }
    bind<CamsRecordRSub>() with singleton { CamsRecordRSubImpl(instance()) }
}
