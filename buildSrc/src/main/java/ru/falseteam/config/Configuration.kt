package ru.falseteam.config

object Configuration {
    object Versions {
        const val kotlin = "1.4.21"
        const val ktor = "1.4.3"
        const val log4j2 = "2.13.3"
    }

    object Dependencies {
        object Kotlin {
            const val stdLibJdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
            const val reflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"
        }

        const val ktor = "io.ktor:ktor-server-netty:${Versions.ktor}"
        const val ktorWebSocket = "io.ktor:ktor-websockets:${Versions.ktor}"
        const val ktorSerialization = "io.ktor:ktor-serialization:${Versions.ktor}"

        const val kodein = "org.kodein.di:kodein-di:7.1.0"

        const val r2dbcH2 = "io.r2dbc:r2dbc-h2:0.8.1.RELEASE"
        const val r2dbcPool = "io.r2dbc:r2dbc-pool:0.8.1.RELEASE"

        const val reactorExtensions = "io.projectreactor.kotlin:reactor-kotlin-extensions:1.0.2.RELEASE"
        const val reactorCoroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.3.5"

        const val graphqlSchemaGenerator = "com.expediagroup:graphql-kotlin-schema-generator:4.0.0-alpha.8"

        const val log4j2Api = "org.apache.logging.log4j:log4j-api:${Versions.log4j2}"
        const val log4j2Core = "org.apache.logging.log4j:log4j-core:${Versions.log4j2}"
        const val log4jSlf4jImpl = "org.apache.logging.log4j:log4j-slf4j-impl:2.9.0"
    }
}
