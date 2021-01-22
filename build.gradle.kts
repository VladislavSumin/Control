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
        classpath(ru.falseteam.config.Configuration.Dependencies.Sqldelight.plugin)
        classpath("com.github.jengelman.gradle.plugins:shadow:6.1.0")
    }
}

plugins {
    id("com.github.ben-manes.versions") version "0.36.0"
    id("io.gitlab.arturbosch.detekt") version "1.14.2"
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
                println(
                    "::warning::Library outdated: " +
                            "${it.group}:${it.name} [${it.version} -> ${it.available.milestone}]"
                )
            }
        }
    }
}

detekt {
    failFast = false // fail build on any finding
    buildUponDefaultConfig = true // preconfigure defaults
    autoCorrect = true
    ignoreFailures = false
    parallel = false

    config = files("$projectDir/config/detekt/detekt.yml")
    reports {
        html.enabled = true // observe findings in your browser with structure and code snippets
        xml.enabled = true // checkstyle like format mainly for integrations like Jenkins
        txt.enabled =
            true // similar to the console output, contains issue signature to manually edit baseline files
//        sarif.enabled =
//            true // SARIF integration (https://sarifweb.azurewebsites.net/) for integrations with Github
    }
}

tasks {
    withType<io.gitlab.arturbosch.detekt.Detekt> {
        this.jvmTarget = "1.8"

        setSource(files(rootDir))
        include("**/*.kt")
        include("**/*.kts")
        exclude("**/build/**")
    }
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.14.2")
}