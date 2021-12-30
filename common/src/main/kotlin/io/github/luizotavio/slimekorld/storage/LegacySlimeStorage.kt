package io.github.luizotavio.slimekorld.storage

import io.github.luizotavio.slimekorld.SlimeKorld
import io.github.luizotavio.slimekorld.SlimeWorld
import io.github.luizotavio.slimekorld.exception.SlimeStorageException
import io.github.luizotavio.slimekorld.impl.LegacySlimeWorld
import io.github.luizotavio.slimekorld.stream.DefaultSlimeCodec
import org.bukkit.World
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld
import java.io.File
import java.io.RandomAccessFile

class LegacySlimeStorage(
    file: File
) : AbstractSlimeWorldStorage(file) {

    override fun save(slimeWorld: SlimeWorld) {
        val file = RandomAccessFile(
            File(path, "${slimeWorld.name}.slime"),
            "rw"
        )

        val world = slimeWorld.getWorld() as CraftWorld

        DefaultSlimeCodec(file).apply {
            write(ArrayList(world.handle.chunkProviderServer.chunks.values()))
        }
    }

    override fun refresh(slimeWorld: SlimeWorld): World {
        if (slimeWorld !is LegacySlimeWorld) {
            throw SlimeStorageException("SlimeWorld must be of type LegacySlimeWorld")
        }

        val target = SlimeKorld.createSlimeWorld(
            slimeWorld.file,
            slimeWorld.name
        ) ?: throw SlimeStorageException("Could not create SlimeWorld")

        slimeWorld.setWorld(
            target.getWorld()
        )

        return target.getWorld()
    }
}