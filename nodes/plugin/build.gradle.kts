plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta15"
}

group = "org.readutf.gameservice"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}