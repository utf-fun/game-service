plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
    mavenLocal()
}
dependencies {

    api(project(":modules:common"))
    api(libs.bundles.tinylog)

    implementation("io.netty:netty-all:4.1.119.Final")

    implementation("net.minestom:minestom:2025.07.04-1.21.5")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}