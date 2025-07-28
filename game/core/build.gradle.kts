plugins {
    `java-library`
}

group = "org.readutf.game"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.panda-lang.org/releases") }
    maven { url = uri("https://mvn.utf.lol/releases") }
}

dependencies {

    compileOnly(libs.bundles.arena)

    api(libs.bundles.buildformat)

    api(libs.hikaricp)
    api(libs.postgresql)

    api(libs.minestom)
    api(project(":discovery:client"))

    testImplementation(libs.junit.jupiter)
//    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test {
    useJUnitPlatform()
}