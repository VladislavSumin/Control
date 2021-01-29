package ru.falseteam.control.domain.analytic

import android.content.Context
import android.util.Log
import io.sentry.android.core.SentryAndroid
import ru.falseteam.control.AppConfig

private const val TAG = "Analytic"

class AnalyticInteractorImpl(
    val context: Context,
) : AnalyticInteractor {
    override fun init() {

        // Skip initialization for debug builds
        if (AppConfig.DEBUG) {
            Log.d(TAG, "Skipping analytic initialization, reason: debug build")
            return
        }

        Log.d(TAG, "initializing SentryAndroid")

        SentryAndroid.init(context) { options ->
            options.dsn =
                "https://2ae3234221634ad9946c044617848430@o512687.ingest.sentry.io/5613349"
            options.sampleRate = 1.0
        }
    }
}