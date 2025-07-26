plugins {
    `java-library`
    id("io.freefair.lombok") version "8.13.1"
}



repositories {
    mavenCentral()
    maven { url = uri("https://mvn.utf.lol/releases") }
}

dependencies {
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)

    api(libs.jetbrains.annotations)
    api(libs.bundles.hermes)
    api(libs.kryo)
    compileOnly(libs.slf4j.api)
}

tasks.test {
    useJUnitPlatform()
}