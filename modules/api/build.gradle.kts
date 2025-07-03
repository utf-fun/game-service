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
    api(project(":modules:common"))

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}


tasks.test {
    useJUnitPlatform()
}