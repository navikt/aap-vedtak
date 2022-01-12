plugins {
    kotlin("jvm") version "1.6.10" apply false
    id("com.github.johnrengelman.shadow") version "7.1.0" apply false
}

subprojects {
    repositories {
        maven("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
        mavenCentral()
    }
}
