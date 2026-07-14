plugins {
    `maven-publish`
}

dependencies {
    compileOnly("net.kyori:adventure-api:4.17.0")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
