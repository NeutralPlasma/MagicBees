plugins {
    kotlin("jvm")
    id("java")
    id("maven-publish")
}

group = "eu.virtusdevelops"
version = "0.0.1"

val minecraftVersion: String by rootProject
val jdkVersion: Int by rootProject


dependencies {
    compileOnly("io.papermc.paper:paper-api:$minecraftVersion-R0.1-SNAPSHOT")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(jdkVersion)
}

publishing {
    repositories {
        maven {
            val releasesRepoUrl = uri("https://nexus3.virtusdevelops.eu/repository/maven-releases/")
            val snapshotsRepoUrl = uri("https://nexus3.virtusdevelops.eu/repository/maven-snapshots/")

            url = uri(if (project.hasProperty("snapshot")) snapshotsRepoUrl else releasesRepoUrl)
            credentials {
                username = System.getenv("NEXUS3_USERNAME") ?: project.findProperty("nexus3User")?.toString()
                password = System.getenv("NEXUS3_PASSWORD") ?: project.findProperty("nexus3Password")?.toString()
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = "eu.virtusdevelops"
            artifactId = "magicbees-api"
            version = version.toString()
        }
    }
}