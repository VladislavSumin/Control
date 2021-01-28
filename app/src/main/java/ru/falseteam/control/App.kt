package ru.falseteam.control

import android.app.Application
import android.content.Context
import io.sentry.android.core.SentryAndroid
import org.kodein.di.DI
import org.kodein.di.DIAware
import ru.falseteam.control.di.Kodein

class App : Application(), DIAware {
    companion object {
        lateinit var instace: App
            private set
    }

    override val di: DI by Kodein

    override fun attachBaseContext(base: Context?) {
        System.setProperty("kotlinx.coroutines.debug", if (BuildConfig.DEBUG) "on" else "off")
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        instace = this

        //TODO move to separate file when add agreements
        SentryAndroid.init(this) { options ->
            options.dsn =
                "https://2ae3234221634ad9946c044617848430@o512687.ingest.sentry.io/5613349"
        }
    }
}