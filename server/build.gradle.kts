import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import ru.falseteam.config.Configuration.Dependencies

plugins {
    application
    kotlin("jvm")
    kotlin("plugin.serialization") version ru.falseteam.config.Configuration.Versions.kotlin
    id("com.squareup.sqldelight")
    id("com.github.johnrengelman.shadow")
    id("com.github.gmazzo.buildconfig")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

project.setProperty("mainClassName", "ru.falseteam.control.server.MainKt")

val pIsBuildAgent: String by project
val pBaseVersionName: String by project
val pBuildNumber: String by project

group = "ru.falseteam.control.server"
version = "$pBaseVersionName.$pBuildNumber"

sqldelight {
    database("Database") {
        packageName = "ru.falseteam.control.server.database"
    }
}

buildConfig {
    className("BuildConfig")
    packageName("ru.falseteam.control.server")
    buildConfigField("String", "version", "\"$version\"")
    buildConfigField("int", "buildNumber", pBuildNumber)
    buildConfigField("boolean", "DEBUG", pIsBuildAgent.toBoolean().not().toString())
}

dependencies {
    implementation(project(":api"))
    implementation(project(":cams-connection"))
    implementation("ru.falseteam.rsub:rsub-ktor-websocket-connector-server")

    with(Dependencies.Kotlin) {
        implementation(stdLibJdk8)
        implementation(reflect)
        implementation(coroutinesDebug)
    }
    with(Dependencies.Ktor) {
        implementation(server)
        implementation(webSocket)
        implementation(serverSerialization)
    }
    with(Dependencies) {
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
