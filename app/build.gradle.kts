val aapLibVersion = "3.7.54"
val ktorVersion = "2.3.3"

plugins {
    id("io.ktor.plugin")
}

application {
    mainClass.set("vedtak.AppKt")
}

dependencies {
    implementation(project(":domene"))
    implementation(project(":dto-kafka"))

    implementation("com.github.navikt.aap-sykepengedager:kafka-dto:1.0.166")
    implementation("com.github.navikt.aap-libs:ktor-utils:$aapLibVersion")
    implementation("com.github.navikt.aap-libs:kafka-2:$aapLibVersion")

    implementation("org.apache.kafka:kafka-clients:3.5.1")

    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    implementation("io.ktor:ktor-server-metrics-micrometer:$ktorVersion")

    implementation("io.micrometer:micrometer-registry-prometheus:1.11.2")
    implementation("ch.qos.logback:logback-classic:1.4.9")
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")

    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("com.github.navikt.aap-libs:kafka-test-2:$aapLibVersion")
}
