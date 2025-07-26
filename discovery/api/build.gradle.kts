plugins {
    id("java-library")
    id("com.google.protobuf") version "0.9.2"
    `maven-publish`
}

repositories {
    mavenCentral()
    maven { url = uri("https://mvn.utf.lol/releases") }
}

dependencies {

    api(libs.retrofit)
    api(libs.retrofit.gson)
    api(libs.okhttp.logging)
    api(project(":discovery:common"))


    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}


tasks.test {
    useJUnitPlatform()
}