plugins {
    id("java-library")
    id("com.google.protobuf") version "0.9.2"
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {

    implementation(libs.retrofit)
    implementation(project(":modules:common"))

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}


tasks.test {
    useJUnitPlatform()
}