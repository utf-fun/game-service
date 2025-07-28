import java.util.*

plugins {
    id("java")
    application
}

repositories {
    mavenCentral()
    maven { url = uri("https://mvn.utf.lol/releases") }
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "org.readutf.gameservice.ServiceStarter"
    }
}

dependencies {

    implementation(project(":discovery:common"))

    implementation(libs.bundles.tinylog)
    implementation(libs.docker)
    implementation(libs.docker.transport)
    implementation(libs.javalin)
    implementation(libs.jackson)
    implementation(libs.kubernetes.client)

    testImplementation(libs.junit.jupiter)
//    testRuntimeOnly(libs.junit.platform.launcher)
}

application {
    mainClass.set("org.readutf.gameservice.ServiceStarter")
}

tasks.jar {
    archiveFileName = "game-service.jar"
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
