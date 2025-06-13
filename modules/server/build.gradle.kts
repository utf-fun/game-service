import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    //shadowjar
    id("com.gradleup.shadow") version "9.0.0-beta15"
}

repositories {
    mavenCentral()
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "org.readutf.gameservice.ServiceStarter"
    }
}

dependencies {

    implementation(project(":modules:common"))

    implementation(libs.bundles.grpc)
    implementation(libs.bundles.tinylog)
    implementation(libs.docker)
    implementation(libs.docker.transport)
    implementation(libs.javalin)
    implementation(libs.jackson)
    implementation("io.kubernetes:client-java:24.0.0")



    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.named<ShadowJar>("shadowJar") {
    archiveFileName.set("discovery-server.jar")
}

tasks.test {
    useJUnitPlatform()
}
