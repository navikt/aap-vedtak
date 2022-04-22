plugins {
    id("com.github.johnrengelman.shadow")
    application
}

application {
    mainClass.set("no.nav.aap.app.AppKt")
}

dependencies {
    implementation(project(":domene"))

    implementation("com.github.navikt.aap-libs:ktor-utils:0.0.18")

    implementation("io.ktor:ktor-server-core:2.0.0")
    implementation("io.ktor:ktor-server-netty:2.0.0")
    implementation("io.ktor:ktor-client-jackson:2.0.0")
    constraints {
        implementation("com.fasterxml.jackson.core:jackson-databind:2.13.2.2") {
            because("2.13.2 vulnerability")
        }
    }

    implementation("io.ktor:ktor-client-core:2.0.0")
    implementation("io.ktor:ktor-client-cio:2.0.0")
    implementation("io.ktor:ktor-server-content-negotiation:2.0.0")
    implementation("io.ktor:ktor-serialization-jackson:2.0.0")

    implementation("io.ktor:ktor-auth:1.6.8")
    implementation("io.ktor:ktor-client-auth:2.0.0")
    implementation("io.ktor:ktor-client-logging:2.0.0")

    implementation("io.ktor:ktor-server-metrics-micrometer:2.0.0")
    implementation("io.micrometer:micrometer-registry-prometheus:1.8.5")

    implementation("com.github.navikt:aap-avro:3.0.2-sokere")
    implementation("com.github.navikt:aap-avro:0.0.3-manuell")
    implementation("com.github.navikt:aap-avro:0.0.2-inntekter")
    implementation("com.github.navikt:aap-avro:1.1.6-medlem")

    implementation("no.nav.security:token-validation-ktor:2.0.14")
    implementation("no.nav.security:token-client-core:2.0.14")


    implementation("ch.qos.logback:logback-classic:1.2.11")
    implementation("io.ktor:ktor-jackson:1.6.8")
    runtimeOnly("net.logstash.logback:logstash-logback-encoder:7.1.1")

    implementation("org.apache.kafka:kafka-clients:3.1.0")
    implementation("org.apache.kafka:kafka-streams:3.1.0")
    constraints {
        implementation("org.rocksdb:rocksdbjni:6.29.4.1") {
            because("Mac M1")
        }
    }
    implementation("io.confluent:kafka-streams-avro-serde:7.1.0") {
        exclude("org.apache.kafka", "kafka-clients")
    }

    // JsonSerializer java 8 LocalDate
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.2")

    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-test-host:2.0.0")
    testImplementation("no.nav.security:mock-oauth2-server:0.4.5")
    // used to override env var runtime
    testImplementation("uk.org.webcompere:system-stubs-jupiter:2.0.1")
    testImplementation("org.apache.kafka:kafka-streams-test-utils:3.1.0")
}
