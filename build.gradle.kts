plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.paper)
}

group = "moe.neat"
version = "2.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly(libs.paper.api)
    implementation(libs.cloud.paper)
    implementation(libs.cloud.kotlin)
}

kotlin {
    jvmToolchain(21)
}

tasks.processResources {
    filesMatching("paper-plugin.yml") {
        expand("version" to version)
    }
}

tasks.shadowJar {
    archiveBaseName.set("multiplayer-hc")
    archiveClassifier.set("")
    archiveVersion.set(version.toString())

    relocate("org.incendo.cloud", "moe.neat.multiplayerhc.lib.cloud")
}

tasks.runServer {
    minecraftVersion("1.21.11")
}
