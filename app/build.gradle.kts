import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/*
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
    application
}

dependencies {
    implementation(project(":domene"))
    implementation("io.ktor:ktor-server-core:1.6.7")
    implementation("io.ktor:ktor-server-netty:1.6.7")
    implementation("io.ktor:ktor-client-jackson:1.6.7")

    implementation("ch.qos.logback:logback-classic:1.2.10")
    implementation("io.ktor:ktor-jackson:1.6.7")
    runtimeOnly("net.logstash.logback:logstash-logback-encoder:7.0.1")

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
