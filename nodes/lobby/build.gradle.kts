import java.util.Properties

plugins {
    kotlin("jvm") version "2.1.0"
    application
    id("com.bmuschko.docker-java-application") version "9.4.0"
}

group = "org.readutf.games.lobby"
version = "1.0.5"

repositories {
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://mvn.utf.lol/releases") }
    maven("https://repo.nexomc.com/releases/")
}

dependencies {
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)

    implementation(libs.minestom)
    implementation(libs.schem)

    implementation(libs.buildformat.common)
    implementation(libs.buildformat.sql)
    implementation(libs.buildformat.s3)

    implementation(libs.postgresql)

    implementation(project(":discovery:client"))
    implementation(libs.ui.toolkit)

    implementation(libs.creative.api)
    implementation(libs.creative.serializer.minecraft)
    implementation(libs.creative.server)


    implementation(libs.bundles.tinylog)
}

application {
    mainClass.set("org.readutf.lobby.Lobby")
}

tasks.withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

tasks.named("installDist") {
    dependsOn("createProperties")
}

tasks.register("createProperties") {
    val propertiesFile = file("$buildDir/resources/main/version.properties")
    propertiesFile.parentFile.mkdirs()
    propertiesFile.writer().use { writer ->
        val properties = Properties()
        properties["version"] = project.version.toString()
        properties["buildTime"] = System.currentTimeMillis().toString()
        properties.store(writer, null)
    }

}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
