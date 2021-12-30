package io.github.luizotavio.slimekorld.stream

import com.github.luben.zstd.Zstd
import io.github.luizotavio.slimekorld.SlimeReaderUtil.readBitSet
import io.github.luizotavio.slimekorld.SlimeReaderUtil.readCompressed
import io.github.luizotavio.slimekorld.SlimeReaderUtil.readCompressedCompound
import io.github.luizotavio.slimekorld.SlimeReaderUtil.skipCompressed
import io.github.luizotavio.slimekorld.SlimeWriterUtil
import io.github.luizotavio.slimekorld.pojo.SlimeFile
import net.minecraft.server.v1_8_R3.Chunk
import net.minecraft.server.v1_8_R3.NBTTagCompound
import java.io.IOException
import java.io.RandomAccessFile
import java.util.*
import kotlin.math.ceil

class DefaultSlimeCodec(
    private val randomAccessFile: RandomAccessFile
) {

    companion object {
        const val FILE_HEADER = 0xB10B.toShort()
    }

    fun write(chunks: MutableList<Chunk>) {
        chunks.removeIf { it.sections.all { section -> Objects.isNull(section) } }

        randomAccessFile.apply {
            writeShort(FILE_HEADER.toInt())

            writeByte(1)

            val lowestX = chunks.stream()
                .mapToInt { it.locX }
                .min()
                .orElse(1)

            val lowestZ = chunks.stream()
                .mapToInt { it.locZ }
                .min()
                .orElse(1)

            writeShort(lowestX)
            writeShort(lowestZ)

            val highestX = chunks.stream()
                .mapToInt { value -> value.locX }
                .max()
                .orElse(1)

            val highestZ = chunks.stream()
                .mapToInt { value -> value.locZ }
                .max()
                .orElse(1)

            val depth = highestZ - lowestZ + 1
            val width = highestX - lowestX + 1

            writeShort(width)
            writeShort(depth)

            val chunkBitset = BitSet(width * depth)

            chunks.sortWith(Comparator.comparingInt { it.locZ * Int.MAX_VALUE + it.locX })

            for (chunk in chunks) {
                chunkBitset[(chunk.locZ - lowestZ) * width + (chunk.locX - lowestX)] = true
            }

            val chunkSize = ceil(width * depth / 8.0).toInt()

            SlimeWriterUtil.writeBitSetAsBytes(randomAccessFile, chunkBitset, chunkSize)

            val chunkData = SlimeWriterUtil.writeCustomChunkFormat(chunks)
            val tileData = SlimeWriterUtil.writeTileEntities(chunks)

            val compressChunkData = Zstd.compress(chunkData)
            val compressTileEntities = Zstd.compress(tileData)

            writeInt(compressChunkData.size)
            writeInt(chunkData.size)
            write(compressChunkData)
            writeInt(compressTileEntities.size)
            writeInt(tileData.size)
            write(compressTileEntities)

            writeBoolean(true)

            val entityData = SlimeWriterUtil.writeEntities(chunks)
            val compressed = Zstd.compress(entityData)

            writeInt(compressed.size)
            writeInt(entityData.size)
            write(compressChunkData)
        }


    }

    fun read(): SlimeFile = randomAccessFile.run {
        val header = readShort()

        if (header != FILE_HEADER) {
            throw IOException("Invalid header: $header")
        }

        val version = read()

        // Lowest chunk coordinates
        val minX = readShort()
        val minZ = readShort()

        // X-axis and Z-axis length respectively
        val width = readShort()
        val depth = readShort()

        val bitSetLength = ceil(width * depth / 8.0).toInt()

        val populatedChunks: BitSet = readBitSet(this, bitSetLength)
        val chunkData: ByteArray = readCompressed(this)
        val tileEntities: NBTTagCompound = readCompressedCompound(this)

        var entities = NBTTagCompound()

        if (version == 3 || version == 1) {
            val hasEntities = readBoolean()

            if (hasEntities) {
                entities = readCompressedCompound(this)
            }

            // Skip extra data
            skipCompressed(this)
        }

        return SlimeFile(
            version,
            minX,
            minZ,
            width,
            depth,
            populatedChunks,
            chunkData,
            entities.getList("entities", 10),
            tileEntities.getList("tiles", 10)
        )
    }

}