plugins {
    `java-library`
    application
}

group = "org.readutf.game"
version = "1.0.0"

repositories {
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://mvn.utf.lol/releases") }
    maven { url = uri("https://repo.panda-lang.org/releases") }
}

dependencies {
    testImplementation(libs.junit.jupiter)
//    testRuntimeOnly(libs.junit.platform.launcher)

    implementation(libs.bundles.arena)
    implementation(project(":game:core"))
    implementation(libs.minestom)

    implementation(libs.minestom.pvp)
}

application {
    mainClass.set("org.readutf.tnttag.TagGameServer")
}

tasks.test {
    useJUnitPlatform()
}