plugins {
    kotlin("jvm")
    id("java")
}

group = "eu.virtusdevelops"
version = "0.0.3"

val minecraftVersion: String by rootProject
val jdkVersion: Int by rootProject


dependencies {
    compileOnly("io.papermc.paper:paper-api:$minecraftVersion-R0.1-SNAPSHOT")

    compileOnly(libs.votingPlugin)
    compileOnly(libs.hikariCP)

    compileOnly(libs.coinsEngine){
        exclude(group = "org.spigotmc", module = "spigot-api")
    }
    compileOnly(libs.vault){
        exclude(group = "org.bukkit", module = "bukkit")
    }

    implementation(project(":api"))

}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(jdkVersion)
}
