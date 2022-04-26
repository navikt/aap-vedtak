import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
}

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
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
                "org.apache.kafka:kafka-clients:3.1.0",
                "org.rocksdb:rocksdbjni:6.29.4.1"
            )
        }
    }
}
