plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
}
dependencies {

    api(project(":modules:common"))
    api(libs.bundles.grpc)
    api(libs.bundles.tinylog)

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}