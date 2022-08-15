import org.gradle.internal.impldep.org.apache.commons.lang.StringUtils

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    maven {
        name = "elmakers"
        url = uri("https://maven.elmakers.com/repository/")
    }

    maven {
        name = "codemc"
        url = uri("https://repo.codemc.org/repository/maven-public/")
    }

    maven {
        name = "jitpack"
        url = uri("https://jitpack.io")
    }
}

val minecraftVersion = "1.8.8-R0.1-SNAPSHOT"

dependencies {
    compileOnly("org.spigotmc:spigot:$minecraftVersion")

    implementation(project(":api"))
    implementation(project(":bukkit"))

    implementation("com.github.SaiintBrisson.command-framework:bukkit:1.3.0")
}

tasks.getByName("jar") {
    enabled = false
}

tasks.shadowJar {
    archiveClassifier.set(StringUtils.EMPTY)
}
