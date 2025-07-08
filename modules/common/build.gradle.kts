plugins {
    `java-library`
    id("io.freefair.lombok") version "8.13.1"
}



repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {

    api("javax.annotation:javax.annotation-api:1.3.1")

    api(libs.jetbrains.annotations)
    compileOnly(libs.slf4j.api)

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    var hermesVersion = "2.1.7"

    api("org.readutf.hermes:core:$hermesVersion")
    api("org.readutf.hermes:netty:$hermesVersion")
    api("org.readutf.hermes:kryo:$hermesVersion")

    //kryo
    api("com.esotericsoftware:kryo:5.6.2")
}

tasks.test {
    useJUnitPlatform()
}