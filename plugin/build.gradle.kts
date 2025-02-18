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
    implementation(libs.bundles.cloudEcosystem)

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
    softDepend = listOf("Vault")
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