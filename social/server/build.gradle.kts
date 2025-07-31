plugins {
    id("java")
}

group = "org.readutf.gameservice"
version = "1.0.20"

repositories {
    mavenCentral()
}

dependencies {


    implementation(project(":social:common"))

    implementation(libs.bundles.tinylog)
    implementation(libs.javalin)
    implementation(libs.jackson)

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}