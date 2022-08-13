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
val nbtApi = "2.10.0"

dependencies {
    compileOnly("org.spigotmc:spigot:$minecraftVersion")

    implementation("de.tr7zw:item-nbt-api-plugin:$nbtApi")
}
