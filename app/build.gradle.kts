val aapLibVersion = "3.5.7"
val ktorVersion = "2.1.2"

plugins {
    id("io.ktor.plugin")
    application
}

application {
    mainClass.set("no.nav.aap.app.AppKt")
}

dependencies {
    implementation(project(":domene"))
    implementation(project(":dto-kafka"))
    implementation("com.github.navikt:aap-sykepengedager:1.0.9")
    implementation("com.github.navikt.aap-libs:ktor-utils:$aapLibVersion")
    implementation("com.github.navikt.aap-libs:kafka:$aapLibVersion")
    testImplementation("com.github.navikt.aap-libs:kafka-test:$aapLibVersion")

    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    implementation("io.ktor:ktor-server-metrics-micrometer:$ktorVersion")

    implementation("io.micrometer:micrometer-registry-prometheus:1.9.5")

    implementation("ch.qos.logback:logback-classic:1.4.4")
    implementation("net.logstash.logback:logstash-logback-encoder:7.2")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.4.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.4")

    testImplementation(kotlin("test"))
}
