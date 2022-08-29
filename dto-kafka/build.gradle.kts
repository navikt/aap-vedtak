plugins {
    `maven-publish`
    `java-library`
}


dependencies {
    api(project(":domene"))
    api("com.github.navikt.aap-libs:kafka:3.1.11")
    testImplementation(kotlin("test"))
}

group = "com.github.navikt"
version = "1.0.0-SNAPSHOT"

tasks {
    withType<Jar> {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.name
            from(components["java"])
        }
    }
}
