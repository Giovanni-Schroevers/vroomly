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
    implementation("com.expediagroup", "graphql-kotlin-ktor-server", "9.0.0-alpha.4")
    implementation("io.ktor:ktor-server-auth:3.3.0")
    implementation("io.ktor:ktor-server-auth-jwt:3.3.0")
    implementation("com.graphql-java:graphql-java-extended-scalars:18.2")
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.datetime)
    implementation("org.postgresql:postgresql:42.7.3")
}
