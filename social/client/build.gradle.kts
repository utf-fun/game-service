plugins {
    id("java-library")
}

group = "org.readutf.social"
version = "1.0.20"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":social:common"))
    implementation(libs.jetbrains.annotations)
    implementation(libs.jackson)

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.java-websocket:Java-WebSocket:1.6.0")
}

tasks.test {
    useJUnitPlatform()
}