plugins {
    id("java")
}

group = "org.readutf.gameservice"
version = "1.0.20"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation(libs.retrofit)
    implementation(libs.jackson)
    implementation(libs.bundles.tinylog)

    implementation("com.squareup.retrofit2:converter-jackson:3.0.0")
    implementation("org.java-websocket:Java-WebSocket:1.6.0")
}

tasks.test {
    useJUnitPlatform()
}