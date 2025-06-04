import com.google.protobuf.gradle.id

plugins {
    `java-library`
    id("com.google.protobuf") version "0.9.2"
    id("io.freefair.lombok") version "8.13.1"
}

group = "org.readutf.game"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    api(libs.bundles.grpc)

    api("javax.annotation:javax.annotation-api:1.3.1")

    api(libs.jetbrains.annotations)
    compileOnly(libs.slf4j.api)

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

protobuf {
    protoc {
        // The artifact spec for the Protobuf Compiler
        artifact = "com.google.protobuf:protoc:3.25.5"
    }
    plugins {
        // Optional: an artifact spec for a protoc plugin, with "grpc" as
        // the identifier, which can be referred to in the "plugins"
        // container of the "generateProtoTasks" closure.
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${libs.versions.grpc.get()}"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc") { }
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}