import java.io.File


fun loadDotEnv(): Map<String, String> {
    val file = rootProject.file(".env")
    if (!file.exists()) return emptyMap()

    return file.readLines()
        .map(String::trim)
        .filter { it.isNotEmpty() && !it.startsWith("#") && it.contains("=") }
        .associate { line ->
            val idx = line.indexOf("=")
            val key = line.substring(0, idx).trim()
            var value = line.substring(idx + 1).trim()
            if ((value.startsWith("\"") && value.endsWith("\"")) ||
                (value.startsWith("'") && value.endsWith("'"))
            ) {
                value = value.substring(1, value.length - 1)
            }
            key to value
        }
}

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
}

group = "com.fsa_profgroep_4"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.core)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    implementation("com.expediagroup", "graphql-kotlin-ktor-server", "9.0.0-alpha.4")
    implementation("io.ktor:ktor-server-auth:3.3.0")
    implementation("io.ktor:ktor-server-auth-jwt:3.3.0")
    implementation("com.graphql-java:graphql-java-extended-scalars:18.2")
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.datetime)
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("at.favre.lib:bcrypt:0.10.2")
}


tasks.named<JavaExec>("run") {
    environment(loadDotEnv())

    doFirst {
        println("Loaded .env entries: ${loadDotEnv().keys}")
    }
}

// Configure tests to use JUnit Platform (JUnit 5)
tasks.withType<Test> {
    useJUnitPlatform()
}
