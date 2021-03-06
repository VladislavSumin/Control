import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import ru.falseteam.config.Configuration.Dependencies

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version ru.falseteam.config.Configuration.Versions.kotlin
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

group = "ru.falseteam.control.api"
version = "0.1.0"

dependencies {
    implementation("ru.falseteam.rsub:rsub-core")
    api(project(":common"))

    with(Dependencies.Kotlin) {
        implementation(stdLibJdk8)
        implementation(coroutines)
        implementation(serialization)
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}
