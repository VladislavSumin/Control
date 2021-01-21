package ru.falseteam.config

object Configuration {
    object Versions {
        const val kotlin = "1.4.21"
        const val ktor = "1.5.0"
        const val log4j2 = "2.13.3"
        const val kodein = "7.1.0"
    }

    object Dependencies {
        object Kotlin {
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

        const val log4j2Api = "org.apache.logging.log4j:log4j-api:${Versions.log4j2}"
        const val log4j2Core = "org.apache.logging.log4j:log4j-core:${Versions.log4j2}"
        const val log4jSlf4jImpl = "org.apache.logging.log4j:log4j-slf4j-impl:2.9.0"
        const val slf4j = "org.slf4j:slf4j-api:1.7.30"
        const val logbackAndroid = "com.github.tony19:logback-android:2.0.0"
        const val setting = "com.russhwolf:multiplatform-settings:0.7"
    }
}
