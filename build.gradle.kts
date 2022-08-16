import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    id("io.ktor.plugin") version "2.1.0" apply false
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://packages.confluent.io/maven/")
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    tasks {
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = "18"
        }

        withType<Test> {
            useJUnitPlatform()
            testLogging {
                events("PASSED", "SKIPPED", "FAILED")
            }
        }
    }

    configurations.all {
        resolutionStrategy {
            force(
                "org.apache.kafka:kafka-clients:3.2.0"
            )
        }
    }
}
