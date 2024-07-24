plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm) apply true
    alias(libs.plugins.ktlint) apply true

    id("maven-publish")
}

allprojects {
    version = "0.0.1"
    group = "io.github.resty-io"

    plugins.apply("java-library")
    plugins.apply("org.jlleitschuh.gradle.ktlint")

    java {
        withJavadocJar()
        withSourcesJar()
    }

    ktlint {
        verbose = true
        outputToConsole = true
        coloredOutput = false
    }
}

publishing {
    repositories {
        maven("https://maven.pkg.github.com/resty-io/resty") {
            name = "github"
            credentials {
                username = project.findProperty("gpr.user") as? String
                password = project.findProperty("gpr.token") as? String
            }
        }
    }
    publications {
        project.subprojects.forEach { project ->
            publications.create<MavenPublication>(project.name) {
                from(components["java"])

                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()
            }
        }
    }
}
