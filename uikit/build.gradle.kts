import ru.falseteam.config.Configuration

plugins {
    id("com.android.library")
    kotlin("android")
}

val pIsBuildAgent: String by project
val pBaseVersionName: String by project
val pBuildNumber: String by project

android {
    compileSdkVersion(Configuration.Android.compileSdkVersion)
    buildToolsVersion = Configuration.Android.buildToolsVersion

    defaultConfig {
        minSdkVersion(Configuration.Android.minSdkVersion)
        targetSdkVersion(Configuration.Android.targetSdkVersion)
        versionCode = pBuildNumber.toInt()
        versionName = "$pBaseVersionName.$pBuildNumber"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled(false)
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Configuration.Versions.compose
    }
}

dependencies {
    with(Configuration.Dependencies.Compose) {
        api(ui)
        api(material)
    }
}