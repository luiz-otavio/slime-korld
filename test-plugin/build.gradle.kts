import me.luizotavio.minecraft.gradle.*

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    maven {
        name = "jitpack"
        url = uri("https://jitpack.io")
    }
}

setupMinecraft()

dependencies {
    implCollection(
        project(":api"),
        project(":bukkit"),
        "com.github.SaiintBrisson.command-framework:bukkit:${Versions.commandFramework}"
    )
}
