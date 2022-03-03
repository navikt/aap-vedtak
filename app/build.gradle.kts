import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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

    implementation("io.ktor:ktor-client-core:1.6.7")
    implementation("io.ktor:ktor-client-cio:1.6.7")

    implementation("io.ktor:ktor-auth:1.6.7")
    implementation("io.ktor:ktor-client-auth:1.6.7")
    implementation("io.ktor:ktor-client-logging:1.6.7")

    implementation("io.ktor:ktor-metrics-micrometer:1.6.7")
    implementation("io.micrometer:micrometer-registry-prometheus:1.8.3")

    implementation("no.nav.aap.avro:sokere:1.1.18")
    implementation("no.nav.aap.avro:manuell:0.0.3")
    implementation("no.nav.aap.avro:inntekter:0.0.2")
    implementation("no.nav.aap.avro:medlem:1.1.6")

    implementation("no.nav.security:token-validation-ktor:2.0.5")
    implementation("no.nav.security:token-client-core:2.0.7")

    implementation("com.sksamuel.hoplite:hoplite-yaml:1.4.16")

    implementation("ch.qos.logback:logback-classic:1.2.10")
    implementation("io.ktor:ktor-jackson:1.6.7")
    runtimeOnly("net.logstash.logback:logstash-logback-encoder:7.0.1")

    implementation("org.apache.kafka:kafka-clients:3.1.0")
    implementation("org.apache.kafka:kafka-streams:3.1.0")
    implementation("io.confluent:kafka-streams-avro-serde:7.0.1") {
        exclude("org.apache.kafka", "kafka-clients")
    }

    // JsonSerializer java 8 LocalDate
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.1")

    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-test-host:1.6.7")
    testImplementation("no.nav.security:mock-oauth2-server:0.4.3")
    // used to override env var runtime
    testImplementation("uk.org.webcompere:system-stubs-jupiter:2.0.1")
    testImplementation("org.apache.kafka:kafka-streams-test-utils:3.1.0")
}

application {
    mainClass.set("no.nav.aap.app.AppKt")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("PASSED", "SKIPPED", "FAILED")
        }
    }
}
