plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
    maven { url = uri("https://mvn.utf.lol/releases") }
}

dependencies {

    api(project(":discovery:common"))
    api(libs.bundles.tinylog)
    api(libs.netty)

    testImplementation(libs.junit.jupiter)
//    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test {
    useJUnitPlatform()
}