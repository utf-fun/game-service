plugins {
    id("java-library")
}

group = "org.readutf.social"
version = "1.0.20"

repositories {
    mavenCentral()
    maven { url = uri("https://mvn.utf.lol/releases") }
}

dependencies {
    api(project(":social:common"))

    implementation(libs.jetbrains.annotations)
    implementation(libs.jackson)
    implementation(libs.retrofit)

    implementation("net.kyori:adventure-api:4.24.0")

    implementation("dev.rollczi:litecommands-framework:3.10.4")
    implementation("dev.rollczi:litecommands-adventure:3.10.4")
//    implementation(project(":litecommands-framework"))

    implementation("com.squareup.retrofit2:converter-jackson:3.0.0")

    implementation("org.java-websocket:Java-WebSocket:1.6.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

}

tasks.test {
    useJUnitPlatform()
}