import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import ru.falseteam.config.Configuration.Dependencies

plugins {
    kotlin("jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

group = "ru.falseteam.control.mqtt-client"
version = "0.1.0"

dependencies {

    with(Dependencies.Kotlin) {
        implementation(stdLibJdk8)
        implementation(coroutines)
    }

    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}
