package io.github.luizotavio.slimekorld.pojo

import io.github.luizotavio.slimekorld.SlimeReaderUtil.readBitSet
import io.github.luizotavio.slimekorld.SlimeReaderUtil.readBlockIds
import io.github.luizotavio.slimekorld.SlimeReaderUtil.readByteArray
import io.github.luizotavio.slimekorld.SlimeReaderUtil.readIntArray
import io.github.luizotavio.slimekorld.SlimeReaderUtil.readNibbleArray
import io.github.luizotavio.slimekorld.SlimeReaderUtil.skipCompressed
import net.minecraft.server.v1_8_R3.*
import java.io.DataInputStream
import java.io.IOException
import java.io.InputStream
import java.io.RandomAccessFile
import java.util.*

data class ProtoSlimeChunk(
    val coords: ChunkCoordIntPair,
    val sections: Array<ChunkSection?>,
    val biomes: ByteArray,
    val heightMap: IntArray,
) {

    companion object {
        private const val HEIGHTMAP_ENTRIES = 256
        private const val BIOMES_LENGTH = 256
        private const val SECTIONS_PER_CHUNK = 16
        private const val BLOCKS_LENGTH = 4096

        @Throws(IOException::class)
        fun read(inputStream: DataInputStream, coords: ChunkCoordIntPair): ProtoSlimeChunk {
            val heightMap: IntArray = readIntArray(inputStream, HEIGHTMAP_ENTRIES)
            val biomes: ByteArray = readByteArray(inputStream, BIOMES_LENGTH)

            // Read sections
            val populatedSections: BitSet = readBitSet(inputStream, SECTIONS_PER_CHUNK / 8)

            val sections = arrayOfNulls<ChunkSection>(SECTIONS_PER_CHUNK)

            for (y in 0 until SECTIONS_PER_CHUNK) {
                if (!populatedSections[y]) {
                    continue
                }

                val yPos = y shl 4
                val section = ChunkSection(yPos, true) // skyLight

                readNibbleArray(inputStream, section.emittedLightArray)

                val blocks: ByteArray = readByteArray(inputStream, BLOCKS_LENGTH)
                val data: NibbleArray = readNibbleArray(inputStream)

                readBlockIds(section.idArray, blocks, data)

                readNibbleArray(inputStream, section.skyLightArray)

                // Skip custom extra data
                skipCompressed(inputStream)

                section.recalcBlockCounts()

                sections[y] = section
            }

            return ProtoSlimeChunk(coords, sections, biomes, heightMap)
        }
    }

    private val tileEntities: MutableList<NBTTagCompound> = ArrayList()
    private val entities: MutableList<NBTTagCompound> = ArrayList()

    fun addTileEntity(compound: NBTTagCompound) {
        tileEntities.add(compound)
    }

    fun addEntity(compound: NBTTagCompound) {
        entities.add(compound)
    }

    /**
     * Adds the entities of this proto chunk to the specified
     * Minecraft chunk.
     *
     * @param world the world the chunk is in
     * @param chunk the chunk to add the entities to
     */
    private fun loadEntities(world: World, chunk: Chunk) {
        for (compound in entities) {
            chunk.g(true)

            var entity = EntityTypes.a(compound, world) ?: continue

            chunk.a(entity)

            // Add riding entities
            var riding = compound
            while (riding.hasKeyOfType("Riding", 10)) {
                val other = EntityTypes.a(
                    riding.getCompound("Riding"), world
                ) ?: break

                chunk.a(other)
                entity.mount(other)

                entity = other
                riding = riding.getCompound("Riding")
            }
        }

        for (compound in tileEntities) {
            val tileEntity = TileEntity.c(compound) ?: continue

            chunk.a(tileEntity)
        }
    }

    /**
     * Converts this proto chunk into a Minecraft chunk.
     *
     * @param world the world the chunk is in
     * @return the loaded chunk
     */
    fun load(world: World) = Chunk(world, coords.x, coords.z).apply {
        a(heightMap)
        d(true) // TerrainPopulated
        e(true) // LightPopulated
        c(0) // InhabitedTime
        a(sections)
        a(biomes)

        loadEntities(world, this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProtoSlimeChunk

        if (coords != other.coords) return false

        return true
    }

    override fun hashCode(): Int {
        return coords.hashCode()
    }
}