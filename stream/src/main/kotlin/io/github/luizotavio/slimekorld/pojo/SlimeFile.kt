package io.github.luizotavio.slimekorld.pojo

import gnu.trove.map.hash.TLongObjectHashMap
import net.minecraft.server.v1_8_R3.ChunkCoordIntPair
import net.minecraft.server.v1_8_R3.NBTTagList
import org.bukkit.craftbukkit.v1_8_R3.util.LongHash
import java.io.DataInputStream
import java.io.IOException
import java.util.*

data class SlimeFile(
    val version: Int,
    val minX: Short,
    val minZ: Short,
    val width: Short,
    val depth: Short,
    val populatedChunks: BitSet,
    val chunkData: ByteArray,
    val entities: NBTTagList,
    val tileEntities: NBTTagList
) {

    val protoChunks = TLongObjectHashMap<ProtoSlimeChunk>()

    init {
        createProtoChunks()
    }

    private fun getChunkCoords(bitIndex: Int): ChunkCoordIntPair {
        return ChunkCoordIntPair(
            bitIndex % width + minX, bitIndex / width + minZ
        )
    }

    /**
     * Gets the proto chunk at the specified block coordinates.
     *
     * @param x the x-coordinate
     * @param z the z-coordinate
     * @return the proto chunk, or `null` if not populated
     */
    fun getProtoChunkAt(x: Int, z: Int): ProtoSlimeChunk? {
        return protoChunks[LongHash.toLong(x shr 4, z shr 4)]
    }

    @Throws(IOException::class)
    private fun createProtoChunks() {
        val stream = chunkData.inputStream()

        DataInputStream(stream).use {
            for (i in 0 until populatedChunks.length()) {
                if (!populatedChunks[i]) {
                    // Non-populated chunk
                    continue
                }

                val coords = getChunkCoords(i)

                protoChunks.put(
                    LongHash.toLong(coords.x, coords.z),
                    ProtoSlimeChunk.read(it, coords)
                )
            }
        }

        loadEntities()
    }

    private fun loadEntities() {
        // Add each entity to its proto chunk
        for (i in 0 until entities.size()) {
            val entityData = entities[i]
            val position = entityData.getList("Pos", 6)
            val x = position.d(0).toInt()
            val z = position.d(2).toInt()
            val chunk = getProtoChunkAt(x, z)
            chunk?.addEntity(entityData)
        }

        // Add each tile entity to its proto chunk
        for (i in 0 until tileEntities.size()) {
            val tileData = tileEntities[i]
            val x = tileData.getInt("x")
            val z = tileData.getInt("z")
            val chunk = getProtoChunkAt(x, z)
            chunk?.addTileEntity(tileData)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SlimeFile

        if (minX != other.minX) return false
        if (minZ != other.minZ) return false
        if (width != other.width) return false
        if (depth != other.depth) return false

        return true
    }

}