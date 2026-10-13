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
    // Minecraft API
    compileOnly(libs.paper.api)

    // Database
    implementation(libs.sqlite.jdbc)

    // Tests
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockk)
    testImplementation(kotlin("test"))
    testImplementation(libs.paper.api)

    testRuntimeOnly(libs.junit.platform.launcher)
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

tasks.shadowJar {
    mergeServiceFiles()

    filesMatching("META-INF/services/**") {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}

tasks.test {
    useJUnitPlatform()
}