package ru.falseteam.config

object Configuration {
    object Versions {
        const val kotlin = "1.4.30"
        const val ktor = "1.5.2"
        const val log4j2 = "2.14.0"
        const val kodein = "7.3.1"
        const val sqldelight = "1.4.4"
        const val sentry = "4.1.0"
        const val compose = "1.0.0-beta01"
        const val coroutines = "1.4.2"
        const val glide = "4.12.0"
        const val activity = "1.3.0-alpha03"
    }

    object Android {
        const val compileSdkVersion = 30
        const val buildToolsVersion = "30.0.3"
        const val minSdkVersion = 26
        const val targetSdkVersion = 30
    }

    object Dependencies {
        object Kotlin {
            const val plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
            const val stdLibJdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
            const val reflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"
            const val coroutines =
                "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
            const val coroutinesDebug =
                "org.jetbrains.kotlinx:kotlinx-coroutines-debug:${Versions.coroutines}"
            const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1"
        }

        //TODO make separate object for ktor
        const val ktorServer = "io.ktor:ktor-server-netty:${Versions.ktor}"
        const val ktorWebSocket = "io.ktor:ktor-websockets:${Versions.ktor}"
        const val ktorClient = "io.ktor:ktor-client-okhttp:${Versions.ktor}"
        const val ktorNetwork = "io.ktor:ktor-network:${Versions.ktor}"
        const val ktorSerialization = "io.ktor:ktor-serialization:${Versions.ktor}"
        const val ktorClientSerialization = "io.ktor:ktor-client-serialization:${Versions.ktor}"

        const val kodein = "org.kodein.di:kodein-di:${Versions.kodein}"
        const val kodeinAndroidX = "org.kodein.di:kodein-di-framework-android-x:${Versions.kodein}"

        const val sentryAndroid = "io.sentry:sentry-android:${Versions.sentry}"
        const val sentry = "io.sentry:sentry:${Versions.sentry}"

        const val exoplayer = "com.google.android.exoplayer:exoplayer:2.13.1"

        const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
        const val glideCompiler = "com.github.bumptech.glide:compiler:${Versions.glide}"

        object Logging {
            const val log4j2Api = "org.apache.logging.log4j:log4j-api:${Versions.log4j2}"
            const val log4j2Core = "org.apache.logging.log4j:log4j-core:${Versions.log4j2}"
            const val log4jSlf4j = "org.apache.logging.log4j:log4j-slf4j-impl:${Versions.log4j2}"
            const val slf4j = "org.slf4j:slf4j-api:1.7.30"
            const val logbackAndroid = "com.github.tony19:logback-android:2.0.0"
        }

        object Sqldelight {
            const val plugin = "com.squareup.sqldelight:gradle-plugin:${Versions.sqldelight}"
            const val sqliteDriver = "com.squareup.sqldelight:sqlite-driver:${Versions.sqldelight}"
            const val coroutineExt =
                "com.squareup.sqldelight:coroutines-extensions-jvm:${Versions.sqldelight}"
        }

        object Compose {
            const val ui = "androidx.compose.ui:ui:${Versions.compose}"
            const val material = "androidx.compose.material:material:${Versions.compose}"
            const val uiTooling = "androidx.compose.ui:ui-tooling:${Versions.compose}"
            const val navigation = "androidx.navigation:navigation-compose:1.0.0-alpha08"
        }

        object Android {
            const val activity = "androidx.activity:activity:${Versions.activity}"
            const val activityCompose = "androidx.activity:activity-compose:${Versions.activity}"
            const val activityKtx = "androidx.activity:activity-ktx:${Versions.activity}"
        }

        const val setting = "com.russhwolf:multiplatform-settings:0.7"
    }
}
