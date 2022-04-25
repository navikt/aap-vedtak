plugins {
    id("com.github.johnrengelman.shadow")
    application
}

application {
    mainClass.set("no.nav.aap.app.AppKt")
}

dependencies {
    implementation(project(":domene"))

    implementation("com.github.navikt.aap-libs:ktor-utils:0.0.37")
    implementation("com.github.navikt.aap-libs:kafka:0.0.37") {
        // fixme: nå bundles kafka-clients:7.0.1 med her av en eller annen grunn.
        exclude("org.apache.kafka", "kafka-clients")
    }

    // fixme: Cannot access class 'io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde'. Check your module classpath for missing or conflicting dependencies
    implementation("io.confluent:kafka-streams-avro-serde:7.0.1") {
        exclude("org.apache.kafka", "kafka-clients")
    }

    implementation("org.apache.kafka:kafka-clients:3.1.0")
    implementation("com.github.navikt:aap-avro:3.0.7")

    implementation("io.ktor:ktor-server-core:2.0.0")
    implementation("io.ktor:ktor-server-netty:2.0.0")

    constraints {
        implementation("com.fasterxml.jackson.core:jackson-databind:2.13.2.2") {
            because("2.13.2 vulnerability")
        }
    }

    implementation("io.ktor:ktor-server-content-negotiation:2.0.0")
    implementation("io.ktor:ktor-serialization-jackson:2.0.0")

    implementation("io.ktor:ktor-server-metrics-micrometer:2.0.0")
    implementation("io.micrometer:micrometer-registry-prometheus:1.8.5")

    implementation("ch.qos.logback:logback-classic:1.2.11")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.2")

    runtimeOnly("net.logstash.logback:logstash-logback-encoder:7.1.1")

    testImplementation(kotlin("test"))
    testImplementation("com.github.navikt.aap-libs:kafka-test:0.0.37")
    testImplementation("io.ktor:ktor-server-test-host:2.0.0")
    testImplementation("uk.org.webcompere:system-stubs-jupiter:2.0.1")
}
