import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import ru.falseteam.config.Configuration.Dependencies

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version ru.falseteam.config.Configuration.Versions.kotlin
    id("com.squareup.sqldelight")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

group = "ru.falseteam.control.server"
version = "0.1.0"

sqldelight {
    database("Database") {
        packageName = "ru.falseteam.control.server.database"
    }
}

dependencies {
    implementation(project(":api"))
    implementation(project(":cams-connection"))
    implementation("ru.falseteam.rsub:rsub-ktor-websocket-connector-server")

    with(Dependencies.Kotlin) {
        implementation(stdLibJdk8)
        implementation(reflect)
    }
    with(Dependencies) {
        implementation(ktorServer)
        implementation(ktorWebSocket)
        implementation(ktorSerialization)

        implementation(kodein)

        implementation(log4j2Api)
        implementation(log4j2Core)
        implementation(log4jSlf4jImpl)
        implementation(slf4j)
    }
    implementation("com.squareup.sqldelight:sqlite-driver:1.4.3")
    implementation("com.squareup.sqldelight:coroutines-extensions-jvm:1.4.3")

    implementation("net.bramp.ffmpeg:ffmpeg:0.6.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}
