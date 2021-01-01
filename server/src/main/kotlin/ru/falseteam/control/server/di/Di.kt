package ru.falseteam.control.server.di

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.kodein.di.*
import ru.falseteam.control.api.rsub.CamsRSub
import ru.falseteam.control.server.database.CameraQueries
import ru.falseteam.control.server.database.Database
import ru.falseteam.control.server.domain.cams.CamsInteractor
import ru.falseteam.control.server.domain.cams.CamsInteractorImpl
import ru.falseteam.control.server.rsub.CamsRSubImpl
import ru.falseteam.rsub.server.RSubServer
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.notExists

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

    // Domain
    bind<CamsInteractor>() with singleton { CamsInteractorImpl(instance()) }

    // rSub
    bind<RSubServer>() with singleton {
        RSubServer().apply {
            registerImpl(instance<CamsRSub>())
        }
    }
    bind<CamsRSub>() with singleton { CamsRSubImpl(instance()) }
}
