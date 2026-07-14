plugins {
    id("com.gradleup.shadow") version "9.5.1" apply false
}

subprojects {
    apply(plugin = "java-library")

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }

    configure<JavaPluginExtension> {
        toolchain.languageVersion = JavaLanguageVersion.of(25)
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    // Pin transitive dependencies pulled in by compileOnly platform APIs (paper-api,
    // velocity-api) that resolve to versions with known CVEs, even though none of these
    // are ever shaded into the shipped jar.
    configurations.all {
        resolutionStrategy {
            force(
                "org.codehaus.plexus:plexus-utils:3.6.1",
                "org.apache.commons:commons-lang3:3.18.0",
                "org.yaml:snakeyaml:2.2",
                "com.google.guava:guava:33.6.0-jre",
            )
        }
    }
}
