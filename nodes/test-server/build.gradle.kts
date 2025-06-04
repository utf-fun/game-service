plugins {
    id("java")
}

group = "org.readutf.game"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":modules:client"))

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}