plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm) apply true
    alias(libs.plugins.ktlint) apply true

    id("maven-publish")
}

allprojects {
    version = "0.1.0"
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
                username = System.getenv("GPR_ACTOR")
                password = System.getenv("GPR_TOKEN")
            }
        }
    }
    publications {
        project.subprojects.forEach { project ->
            publications.create<MavenPublication>(project.name) {
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()

                from(project.components["java"])
            }
        }
    }
}
