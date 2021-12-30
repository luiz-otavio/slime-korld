package io.github.luizotavio.slimekorld

import net.minecraft.server.v1_8_R3.*
import java.io.*
import java.util.*

object SlimeWriterUtil {

    @Throws(IOException::class)
    fun writeBitSetAsBytes(output: DataOutput, set: BitSet, fixedSize: Int) {
        val array = set.toByteArray()

        output.write(array)

        for (i in 0 until (fixedSize - array.size)) {
            output.write(0)
        }
    }

    fun writeEntities(chunks: List<Chunk>): ByteArray {
        val compound = NBTTagCompound()
        val nbtTagList = NBTTagList()

        for (chunk in chunks) {
            val entities = mutableSetOf<Entity>()

            for (behavior in chunk.getEntitySlices()) {
                entities.addAll(behavior)
            }

            for (entity in entities) {
                val nbtTagCompound = NBTTagCompound()
                    .also { entity.e(it) }

                nbtTagList.add(nbtTagCompound)
            }
        }

        return writeCompound(
            compound.also { it["entities"] = nbtTagList }
        )
    }

    fun writeCompound(compound: NBTTagCompound): ByteArray = ByteArrayOutputStream()
        .apply {
            NBTCompressedStreamTools.a(compound, this)
        }.toByteArray()

    fun writeTileEntities(chunks: List<Chunk>): ByteArray {
        val list = NBTTagList()

        for (chunk in chunks) {
            for (tileEntity in chunk.getTileEntities().values) {
                val nbtTagCompound = NBTTagCompound()
                    .also { tileEntity.b(it) }

                list.add(nbtTagCompound)
            }
        }

        return writeCompound(
            NBTTagCompound().also { it["tiles"] = list }
        )
    }

    @Throws(IOException::class)
    fun writeCustomChunkFormat(chunks: List<Chunk>): ByteArray {
        val outputStream = ByteArrayOutputStream(16384)

        DataOutputStream(outputStream).use {
            for (chunk in chunks) {
                for (value in chunk.heightMap) {
                    it.writeInt(value)
                }

                it.write(chunk.biomeIndex)

                val sections = chunk.sections
                val sectionBitmask = BitSet(16)

                for (i in 0..15) {
                    sectionBitmask[i] = i < sections.size && sections[i] != null
                }

                writeBitSetAsBytes(it, sectionBitmask, 2)

                for (section in sections) {
                    if (section != null) {
                        val objects = writeSection(section)

                        it.write(section.emittedLightArray.a())
                        it.write(objects.first)
                        it.write(objects.second.a())
                        it.write(section.skyLightArray.a())

                        it.writeShort(0)
                    }
                }
            }
        }

        return outputStream.toByteArray()
    }

    fun writeSection(chunkSection: ChunkSection): Pair<ByteArray, NibbleArray> {
        val ids = chunkSection.idArray

        val bytes = ByteArray(ids.size)
        val array = NibbleArray()

        for (k in ids.indices) {
            val c0 = ids[k]

            val l = k and 15
            val i1 = k shr 8 and 15
            val j1 = k shr 4 and 15
            bytes[k] = (c0.code shr 4 and 255).toByte()

            array.a(l, i1, j1, c0.code and 15)
        }

        return Pair(bytes, array)
    }

}