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