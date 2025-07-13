plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
    maven { url = uri("https://mvn.utf.lol/releases") }
}
dependencies {

    api(project(":modules:common"))
    api(libs.bundles.tinylog)
    api("io.netty:netty-all:4.1.119.Final")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}