package io.github.luizotavio.slimekorld.loader

import gnu.trove.map.hash.TLongObjectHashMap
import io.github.luizotavio.slimekorld.pojo.ProtoSlimeChunk
import io.github.luizotavio.slimekorld.stream.DefaultSlimeCodec
import net.minecraft.server.v1_8_R3.Chunk
import net.minecraft.server.v1_8_R3.IChunkLoader
import net.minecraft.server.v1_8_R3.World
import org.bukkit.craftbukkit.v1_8_R3.util.LongHash
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile

class SlimeChunkLoader(
    file: File
) : IChunkLoader {

    private val protoChunks: TLongObjectHashMap<ProtoSlimeChunk>
    private val loadedChunks: TLongObjectHashMap<Chunk> = TLongObjectHashMap()

    init {
        if (!file.exists()) {
            throw IOException("File not found: $file")
        }

        val randomAccessFile = RandomAccessFile(file, "r")

        protoChunks = DefaultSlimeCodec(randomAccessFile)
            .run { read().protoChunks }
    }

    /**
     * Converts the proto chunk at the specified coordinates
     * into a Minecraft chunk.
     *
     * @param world the world the chunk is in
     * @return the loaded chunk, or `null` if not populated
     */
    private fun loadProtoChunk(world: World, hash: Long): Chunk? {
        val proto: ProtoSlimeChunk = protoChunks.remove(hash) ?: return null

        val loaded: Chunk = proto.load(world)

        loadedChunks.put(hash, loaded)
        return loaded
    }

    /**
     * Loads the chunk at the specified chunk coordinates.
     *
     * @param world the world
     * @param chunkX the chunk x-coordinate
     * @param chunkZ the chunk z-coordinate
     * @return the loaded chunk, or `null` if couldn't load
     */
    override fun a(world: World, chunkX: Int, chunkZ: Int): Chunk? {
        val hash = LongHash.toLong(chunkX, chunkZ)
        var chunk = loadedChunks[hash]
        if (chunk == null) {
            chunk = loadProtoChunk(world, hash)
        }
        return chunk
    }

    override fun a(world: World?, chunk: Chunk?) {
        // NOOP: saveChunk
    }

    @Throws(IOException::class)
    override fun b(world: World?, chunk: Chunk?) {
        // NOOP: writeChunkToNBT
    }

    override fun a() {
        // NOOP: chunkTick
    }

    override fun b() {
        // NOOP: saveExtraData
    }
}