plugins {
    id("java")
}

group = "org.readutf.game"
version = "1.0.0"

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "java")

    group = rootProject.group
    version = rootProject.version

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }
}