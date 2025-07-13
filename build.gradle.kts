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

    if(name in listOf("client", "common", "api")) {
        apply(plugin = "maven-publish")

        publishing {
            publications {
                create<MavenPublication>("maven") {
                    groupId = project.group as String
                    artifactId = project.name
                    version = project.version as String

                    from(components["java"])
                }
            }

            repositories {
                maven {
                    name = "utfMvn"
                    url = uri("https://mvn.utf.lol/releases")
                    credentials {
                        username = System.getenv("UTF_MVN_USER") ?: findProperty("utfMvnUser")?.toString() ?: ""
                        password = System.getenv("UTF_MVN_PASS") ?: findProperty("utfMvnPass")?.toString() ?: ""
                    }
                }

            }

        }
    }

}