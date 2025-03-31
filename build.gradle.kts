plugins {
    kotlin("jvm") version "1.9.10"
    application
}

group = "theo.challenge"
version = "1.0-FINAL"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.vertx:vertx-core:4.5.1")
    implementation("io.vertx:vertx-web:4.5.1")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.0")

    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-classic:1.4.11")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    mainClass.set("theo/challenge/orderbook/OrderBookApplication.kt")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "17"
}

tasks.test {
    useJUnitPlatform()
}