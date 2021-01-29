package ru.falseteam.control

import android.app.Application
import android.content.Context
import android.util.Log
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.direct
import org.kodein.di.instance
import ru.falseteam.control.di.Kodein
import ru.falseteam.control.domain.analytic.AnalyticInteractor

private const val TAG = "App"

class App : Application(), DIAware {
    companion object {
        lateinit var instace: App
            private set
    }

    override val di: DI by Kodein

    override fun attachBaseContext(base: Context?) {
        Log.i(TAG, "attachBaseContext()")
        System.setProperty("kotlinx.coroutines.debug", if (BuildConfig.DEBUG) "on" else "off")
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate()")
        instace = this

        direct.instance<AnalyticInteractor>().init()
    }
}