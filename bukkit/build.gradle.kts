import me.luizotavio.minecraft.gradle.*

plugins {
    id("com.github.johnrengelman.shadow") version Versions.shadowJar
    id("maven-publish")
}

setupMinecraft()

dependencies {
    implCollection(
        project(":api"),
        "com.github.luben:zstd-jni:${Versions.zstdVersion}",
        "de.tr7zw:item-nbt-api-plugin:${Versions.nbtApi}"
    )
}

tasks.shadowJar {
    // Exclude plugin-yml due to item-nbt-api
    minimize {
        exclude("*.yml")
    }
}

publishing {
    publications {
        create<MavenPublication>("Jitpack") {
            project.shadow.component(this)

            from(components["java"])

            groupId = "me.luizotavio.minecraft"
            artifactId = "slime-korld"
            version = Versions.minecraftVersion

            pom {
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://choosealicense.com/licenses/mit/")
                    }
                }

                url.set("https://github.com/luiz-otavio")

                developers {
                    developer {
                        id.set("luiz-otavio")
                        name.set("Luiz Otavio")
                        email.set("luizfarrea@gmail.com")
                    }
                }
            }
        }
    }
}
