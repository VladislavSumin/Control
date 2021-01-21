import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.benmanes.gradle.versions.reporter.result.Result

buildscript {
    repositories {
        mavenCentral()
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0-alpha04")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.21")
        classpath("com.squareup.sqldelight:gradle-plugin:1.4.3")
        classpath("com.github.jengelman.gradle.plugins:shadow:6.1.0")
    }
}

plugins {
    id("com.github.ben-manes.versions") version "0.36.0"
}

allprojects {
    repositories {
        mavenCentral()
        google()
        jcenter()
    }
}

tasks.create("clean", Delete::class) {
    delete(rootProject.buildDir)
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

val pIsBuildAgent: String by project

tasks.named("dependencyUpdates", DependencyUpdatesTask::class.java) {
    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }

    if (pIsBuildAgent.toBoolean()) {
        this.outputFormatter = closureOf<Result> {
            outdated.dependencies.forEach {
                println("::warning::Library outdated: ${it.group}:${it.name} [${it.version} -> ${it.available.milestone}]")
            }
        }
    }
}
