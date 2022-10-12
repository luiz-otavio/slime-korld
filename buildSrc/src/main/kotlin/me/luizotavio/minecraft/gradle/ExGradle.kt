@file:JvmName("ExGradle")

package me.luizotavio.minecraft.gradle

import org.gradle.api.Project
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.repositories

fun Project.setupMinecraft(version: String? = "1.8.8-R0.1-SNAPSHOT") {
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

    dependencies {
        add("compileOnly", "org.spigotmc:spigot:$version")
    }
}

fun DependencyHandlerScope.implCollection(vararg dependencies: Any) {
    dependencies.forEach {
        add("implementation", it)
    }
}

object Versions {

    const val shadowJar = "7.1.2"

    // Latest stable
    const val commandFramework = "1.2.0"

    const val nbtApi = "2.10.0"
    const val zstdVersion = "1.5.2-3"

    const val minecraftVersion = "1.8.8-R0.1-SNAPSHOT"
}
