import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/*
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
    kotlin("jvm")
    application
}

dependencies {
    implementation(project(":domene"))
    testImplementation(kotlin("test"))
}

application {
//     Define the main class for the application.
    mainClass.set("no.nav.aap.app.AppKt")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    withType<Test> {
        useJUnitPlatform()
    }
}
