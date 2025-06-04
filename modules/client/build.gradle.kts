plugins {
    `java-library`
}



repositories {
    mavenCentral()
}

dependencies {

    api(project(":modules:common"))
    api(libs.bundles.grpc)

    api("org.tinylog:tinylog-api:2.7.0")
    api("org.tinylog:tinylog-impl:2.7.0")
    api("org.tinylog:slf4j-tinylog:2.7.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}