plugins {
    id("com.gradle.enterprise") version "3.5.1"
}

rootProject.name = "Control"
includeBuild("../rSub")
include(":app", ":server", ":api", "cams-connection")

val pIsBuildAgent: String by settings

gradleEnterprise {
    buildScan {
        publishAlwaysIf(pIsBuildAgent.toBoolean())
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        isUploadInBackground = false
    }
}