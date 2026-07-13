dependencies {
    implementation(project(":common"))
    compileOnly("com.velocitypowered:velocity-api:3.4.0")
}

tasks.processResources {
    val props = mapOf("version" to version)
    filesMatching("velocity-plugin.json") {
        expand(props)
    }
}
