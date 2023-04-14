plugins {
    kotlin("jvm") version "1.8.20"
    id("io.ktor.plugin") version "2.2.4" apply false
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    tasks {
        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions.jvmTarget = "19"
        }

        withType<Test> {
            reports.html.required.set(false)
            useJUnitPlatform()
            maxParallelForks = Runtime.getRuntime().availableProcessors()
        }
    }

    kotlin.sourceSets["main"].kotlin.srcDirs("main")
    kotlin.sourceSets["test"].kotlin.srcDirs("test")
    sourceSets["main"].resources.srcDirs("main")
    sourceSets["test"].resources.srcDirs("test")
}
