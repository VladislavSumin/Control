package ru.falseteam.config

object Configuration {
    object Versions {
        const val kotlin = "1.4.21"
        const val ktor = "1.5.0"
        const val log4j2 = "2.14.0"
        const val kodein = "7.2.0"
        const val sqldelight = "1.4.4"
    }

    object Dependencies {
        object Kotlin {
            const val plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
            const val stdLibJdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
            const val reflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"
            const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2"
            const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1"
        }

        const val ktorServer = "io.ktor:ktor-server-netty:${Versions.ktor}"
        const val ktorWebSocket = "io.ktor:ktor-websockets:${Versions.ktor}"
        const val ktorClient = "io.ktor:ktor-client-okhttp:${Versions.ktor}"
        const val ktorNetwork = "io.ktor:ktor-network:${Versions.ktor}"
        const val ktorSerialization = "io.ktor:ktor-serialization:${Versions.ktor}"
        const val ktorClientSerialization = "io.ktor:ktor-client-serialization:${Versions.ktor}"

        const val kodein = "org.kodein.di:kodein-di:${Versions.kodein}"
        const val kodeinAndroidX = "org.kodein.di:kodein-di-framework-android-x:${Versions.kodein}"

        const val sentryAndroid = "io.sentry:sentry-android:3.1.0"
        const val sentry = "io.sentry:sentry:4.0.0"

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

        const val setting = "com.russhwolf:multiplatform-settings:0.7"
    }
}
