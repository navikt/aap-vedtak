plugins {
    id("com.github.johnrengelman.shadow")
    application
}

application {
    mainClass.set("no.nav.aap.app.AppKt")
}

dependencies {
    implementation(project(":domene"))

    implementation("com.github.navikt.aap-libs:ktor-utils:2.0.7")
    implementation("com.github.navikt.aap-libs:kafka:2.0.7")
    testImplementation("com.github.navikt.aap-libs:kafka-test:2.0.7")

    implementation("com.github.navikt.aap-avro:medlem:3.0.9")

    implementation("io.ktor:ktor-server-core:2.0.2")
    implementation("io.ktor:ktor-server-netty:2.0.2")
    implementation("io.ktor:ktor-server-content-negotiation:2.0.2")
    implementation("io.ktor:ktor-serialization-jackson:2.0.2")
    implementation("io.ktor:ktor-server-metrics-micrometer:2.0.2")

    implementation("io.micrometer:micrometer-registry-prometheus:1.9.0")

    implementation("ch.qos.logback:logback-classic:1.2.11")
    runtimeOnly("net.logstash.logback:logstash-logback-encoder:7.2")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.3")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.3")

    testImplementation(kotlin("test"))

    testImplementation("io.ktor:ktor-server-test-host:2.0.2")
    testImplementation("uk.org.webcompere:system-stubs-jupiter:2.0.1")
}
