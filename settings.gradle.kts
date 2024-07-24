pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            version("kotlin", "2.0.0")
            version("kotlinx-coroutines", "1.9.0-RC")
            version("kotlinx-serialization", "1.7.1")
            version("okhttp", "4.12.0")
            version("ktlint", "12.1.1")

            library(
                "kotlin-stdlib",
                "org.jetbrains.kotlin",
                "kotlin-stdlib",
            ).versionRef("kotlin")
            library(
                "kotlinx-coroutines-core",
                "org.jetbrains.kotlinx",
                "kotlinx-coroutines-core",
            ).versionRef("kotlinx-coroutines")
            library(
                "kotlinx-serialization-json",
                "org.jetbrains.kotlinx",
                "kotlinx-serialization-json",
            ).versionRef("kotlinx-serialization")

            library(
                "okhttp",
                "com.squareup.okhttp3",
                "okhttp",
            ).versionRef("okhttp")

            plugin(
                "jetbrains-kotlin-jvm",
                "org.jetbrains.kotlin.jvm",
            ).versionRef("kotlin")
            plugin(
                "ktlint",
                "org.jlleitschuh.gradle.ktlint",
            ).versionRef("ktlint")
        }
    }
}

rootProject.name = "resty"

include("resty-core")
include("resty-okhttp")
include("resty-serialization-json")
