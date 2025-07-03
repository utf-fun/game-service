plugins {
    `java-library`
    id("io.freefair.lombok") version "8.13.1"
}



repositories {
    mavenCentral()
    maven { url = uri("https://mvn.utf.lol/releases") }
}

dependencies {

    api("javax.annotation:javax.annotation-api:1.3.1")

    api(libs.jetbrains.annotations)
    compileOnly(libs.slf4j.api)

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    api("org.readutf.hermes:core:2.0.3")
    api("org.readutf.hermes:nio:2.0.3")
    api("org.readutf.hermes:kryo:2.0.3")

    //kryo
    api("com.esotericsoftware:kryo:5.6.2")
}

tasks.test {
    useJUnitPlatform()
}