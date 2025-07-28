plugins {
    id("java")
    `maven-publish`
}

group = "org.readutf.gameservice"
version = "1.0.20"

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
        withSourcesJar()
    }

}