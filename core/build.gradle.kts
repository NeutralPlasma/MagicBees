plugins {
    kotlin("jvm")
    id("java")
}

group = "eu.virtusdevelops"
version = "0.0.1"

val minecraftVersion: String by rootProject
val jdkVersion: Int by rootProject


dependencies {
    compileOnly("io.papermc.paper:paper-api:$minecraftVersion-R0.1-SNAPSHOT")
    compileOnly(libs.vault)
    compileOnly(libs.votingPlugin)

    implementation(project(":api"))

}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(jdkVersion)
}
