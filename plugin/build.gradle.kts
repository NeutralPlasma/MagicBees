import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    kotlin("jvm")
    id("java")
    id("com.gradleup.shadow") version "8.3.5"
    id("de.eldoria.plugin-yml.bukkit") version "0.6.0"
}

group = "eu.virtusdevelops"
version = "unspecified"

val minecraftVersion: String by rootProject
val jdkVersion: Int by rootProject

dependencies {
    compileOnly("io.papermc.paper:paper-api:$minecraftVersion-R0.1-SNAPSHOT")

    implementation(project(":core"))
    implementation(project(":api"))
    implementation(project(":gui"))

    compileOnly(libs.bundles.cloudEcosystem)

    implementation("org.bstats:bstats-bukkit:3.0.2")
}


tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(jdkVersion)
}

val authorsList = listOf("VirtusDevelops")
bukkit {
    name = "MagicBees"
    main = "eu.virtusdevelops.magicbees.plugin.MagicBeesPlugin"
    apiVersion = "1.21"
    foliaSupported = false
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    authors = authorsList
    softDepend = listOf("Vault", "CoinsEngine", "VotingPlugin")
    libraries = listOf(
        "org.incendo:cloud-core:2.0.0",
        "org.incendo:cloud-annotations:2.0.0",
        "org.incendo:cloud-paper:2.0.0-beta.10",
        "org.incendo:cloud-minecraft-extras:2.0.0-beta.10",

        "net.kyori:adventure-platform-bukkit:4.3.4",

        "com.zaxxer:HikariCP:5.0.1",
        "com.h2database:h2:2.3.232"
    )
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        archiveBaseName.set("MagicBees")
        //dependsOn(":api:shadowJar")
    }

    build {
        dependsOn(shadowJar)
    }
}