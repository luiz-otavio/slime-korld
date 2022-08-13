import org.gradle.internal.impldep.org.apache.commons.lang.StringUtils

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("maven-publish")
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
}

val minecraftVersion = "1.8.8-R0.1-SNAPSHOT"
val zstdVersion = "1.5.2-3"
val nbtApi = "2.10.0"

dependencies {
    compileOnly("org.spigotmc:spigot:$minecraftVersion")

    implementation(
        project(":api")
    )

    implementation("com.github.luben:zstd-jni:$zstdVersion")
    implementation("de.tr7zw:item-nbt-api-plugin:$nbtApi")
}

tasks.getByName("jar") {
    enabled = false
}

tasks.shadowJar {
    archiveClassifier.set(StringUtils.EMPTY)

    // Exclude plugin-yml due to item-nbt-api
    minimize {
        exclude("*.yml")
    }
}

publishing {
    publications {
        create<MavenPublication>("Jitpack") {
            project.shadow.component(this)

            groupId = "me.luizotavio.minecraft"
            artifactId = "slime-korld"
            version = minecraftVersion

            pom {
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://choosealicense.com/licenses/mit/")
                    }
                }

                url.set("https://github.com/luiz-otavio")
            }
        }
    }
}
