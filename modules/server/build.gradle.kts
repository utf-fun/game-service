plugins {
    id("java")
}



repositories {
    mavenCentral()
}

dependencies {

    implementation(project(":modules:common"))

    implementation(libs.bundles.grpc)
    implementation(libs.bundles.tinylog)
    implementation(libs.docker)
    implementation(libs.docker.transport)
    implementation(libs.javalin)



    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
