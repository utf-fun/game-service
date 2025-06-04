plugins {
    id("java")
}



repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":modules:client"))

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}