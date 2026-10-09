plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.shadow)
}

group = "me.gblrod"
version = "0.1.0"

repositories {
    mavenCentral()

    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly(libs.paper.api)

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(27)

    compilerOptions {
        jvmTarget.set(
            org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25
        )
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

tasks.test {
    useJUnitPlatform()
}