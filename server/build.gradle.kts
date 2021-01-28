import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import ru.falseteam.config.Configuration.Dependencies

plugins {
    application
    kotlin("jvm")
    kotlin("plugin.serialization") version ru.falseteam.config.Configuration.Versions.kotlin
    id("com.squareup.sqldelight")
    id("com.github.johnrengelman.shadow")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

project.setProperty("mainClassName", "ru.falseteam.control.server.MainKt")

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
        implementation(setting)
        implementation(sentry)
    }
    with(Dependencies.Logging) {
        implementation(log4j2Api)
        implementation(log4j2Core)
        implementation(log4jSlf4j)
        implementation(slf4j)
    }
    with(Dependencies.Sqldelight) {
        implementation(sqliteDriver)
        implementation(coroutineExt)
    }

    implementation("net.bramp.ffmpeg:ffmpeg:0.6.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks.withType<ShadowJar> {
    archiveBaseName.set("control")
    archiveClassifier.set("")
}
