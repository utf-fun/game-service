plugins {
    id("java")
    //grpc protobufs
    id("com.google.protobuf") version "0.9.2"
}

group = "org.readutf.game"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("javax.annotation:javax.annotation-api:1.3.1")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}



tasks.test {
    useJUnitPlatform()
}