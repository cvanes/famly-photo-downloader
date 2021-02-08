import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.30"
}

group = "com.cvanes"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
}

val ktorVersion = "1.5.1"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.slf4j:slf4j-simple:1.7.30")
    implementation("info.picocli:picocli:4.6.1")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("io.ktor:ktor-client-jackson:$ktorVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.10.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

task<JavaExec>("photos") {
    group = "famly"
    description = "Download a batch of photos from Famly"
    main = "com.cvanes.famly.cli.PhotoDownloaderKt"
    classpath = sourceSets["main"].runtimeClasspath
}.dependsOn("build")
