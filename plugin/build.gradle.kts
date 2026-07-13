plugins {
    id("com.gradleup.shadow")
}

dependencies {
    implementation(project(":bukkit"))
    implementation(project(":velocity"))
}

tasks {
    shadowJar {
        archiveBaseName.set("RealyEasyVanish")
        archiveClassifier.set("")
        relocate("org.yaml.snakeyaml", "fr.mathilde.realyEasyVanish.libs.snakeyaml")
    }

    build {
        dependsOn(shadowJar)
    }
}
