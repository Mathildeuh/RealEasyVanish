plugins {
    `maven-publish`
}

dependencies {
    compileOnly("net.kyori:adventure-api:4.17.0")
}

// Cette API est le seul module pensé pour être consommé par des plugins tiers (via JitPack) —
// contrairement à common/bukkit/velocity/plugin qui tournent uniquement dans notre propre process
// (déjà sur le JDK 25 requis par le reste du build). Sans ce override du toolchain 25 hérité de la
// racine, les .class produits ici seraient illisibles par un consommateur encore sur JDK 17/21
// (ex: Velocity ne requiert que le JDK 17), qui plantera au chargement des classes ou même dès la
// compilation (javac refuse de lire un class file d'une version supérieure à son propre JDK).
java {
    toolchain.languageVersion = JavaLanguageVersion.of(17)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
