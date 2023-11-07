plugins {
    id("java")
    kotlin("jvm") version "1.9.0"
}

group = "oreo.inu"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(files(file(System.getProperty("user.home")).resolve("inubot").resolve("inubot.jar").path))
}

tasks.register<Copy>("copyToInuDir") {
    from(tasks.jar)
    into(file(System.getProperty("user.home")).resolve("inubot").resolve("scripts"))
}

tasks.jar {
    archiveFileName.set("oreo-inu.jar")
    // Copy all dependencies into the jar
    from({ configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) } })
    // Don't copy META-INF folder from the jar
    exclude("META-INF/**")
    finalizedBy("copyToInuDir")
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "11"
    targetCompatibility = "11"
    sourceSets["main"].java.srcDirs("src/main/kotlin", "src/main/java")
}

kotlin {
    jvmToolchain(11)
}