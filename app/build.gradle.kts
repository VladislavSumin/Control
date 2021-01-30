import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import ru.falseteam.config.Configuration

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.serialization") version ru.falseteam.config.Configuration.Versions.kotlin
}

val pIsBuildAgent: String by project
val pBaseVersionName: String by project
val pBuildNumber: String by project

android {
    compileSdkVersion(Configuration.Android.compileSdkVersion)
    buildToolsVersion = Configuration.Android.buildToolsVersion

    defaultConfig {
        applicationId = "ru.falseteam.control"
        minSdkVersion(Configuration.Android.minSdkVersion)
        targetSdkVersion(Configuration.Android.targetSdkVersion)
        versionCode = pBuildNumber.toInt()
        versionName = "$pBaseVersionName.$pBuildNumber"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("shared") {
            storeFile = file("../secrets/false-debug.jks")
            storePassword = "Qwerty12"
            keyAlias = "debug"
            keyPassword = "Qwerty12"
        }

        if (pIsBuildAgent.toBoolean()) {
            val pReleaseKeystorePassword = System.getenv()["RELEASE_KEYSTORE_PASSWORD"]

            create("release") {
                storeFile = file("../secrets/false-release.jks")
                storePassword = pReleaseKeystorePassword
                keyAlias = "release"
                keyPassword = pReleaseKeystorePassword
            }
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("shared")
        }
        release {
            minifyEnabled(false)
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName(
                if (pIsBuildAgent.toBoolean()) "release" else "shared"
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

// Change apk name
android.applicationVariants.all variant@{
    outputs.all {
        this as BaseVariantOutputImpl
        outputFileName = "Control-${android.defaultConfig.versionName}-${this@variant.name}.apk"
    }
}

dependencies {
    implementation(project(":api"))
    implementation(project(":uikit"))
    implementation("ru.falseteam.rsub:rsub-ktor-websocket-connector-client")

    with(Configuration.Dependencies) {
        implementation(kodein)
        implementation(kodeinAndroidX)
        implementation(ktorClient)
        implementation(ktorClientSerialization)
        implementation(setting)
        implementation(sentryAndroid)
        implementation(exoplayer)
    }

    with(Configuration.Dependencies.Logging) {
        implementation(logbackAndroid)
    }

    with(Configuration.Dependencies.Compose) {
        implementation(navigation)
    }

    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.2.1")
//    testImplementation("junit:junit:4.13.1")
//    androidTestImplementation("androidx.test.ext:junit:1.1.2")
//    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}