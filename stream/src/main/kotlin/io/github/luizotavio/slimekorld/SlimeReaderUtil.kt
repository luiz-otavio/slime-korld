package io.github.luizotavio.slimekorld

import com.github.luben.zstd.Zstd
import net.minecraft.server.v1_8_R3.Block
import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools
import net.minecraft.server.v1_8_R3.NBTTagCompound
import net.minecraft.server.v1_8_R3.NibbleArray
import java.io.*
import java.util.*

object SlimeReaderUtil {

    /**
     * Gets the block array index of the block specified
     * by the chunk section coordinates.
     *
     * @param x the chunk section x-coordinate
     * @param y the chunk section y-coordinate
     * @param z the chunk section z-coordinate
     * @return the index
     */
    fun getBlockIndex(x: Int, y: Int, z: Int): Int {
        return y shl 8 or (z shl 4) or x
    }

    /**
     * Converts the specified block and block data arrays to
     * internal block IDs.
     *
     * @param blockIds the array to write the ids to
     * @param blocks the block array
     * @param data the block data array
     */
    fun readBlockIds(blockIds: CharArray, blocks: ByteArray, data: NibbleArray) {
        for (i in blockIds.indices) {
            val x = i and 0xF
            val y = i shr 8 and 0xF
            val z = i shr 4 and 0xF

            val id = blocks[i].toInt() and 0xFF

            blockIds[i] = getBlockId(id, data.a(x, y, z))
        }
    }

    fun getBlockId(id: Int, blockData: Int): Char {
        var packed = id shl 4 or blockData
        if (Block.d.a(packed) == null) {
            // Convert old block
            val block = Block.getById(id)
            if (block != null) {
                try {
                    block.toLegacyData(block.fromLegacyData(blockData))
                } catch (ignored: Exception) {
                    block.toLegacyData(block.blockData)
                }

                // Recompute packed ID
                packed = id shl 4 or blockData
            }
        }
        return packed.toChar()
    }

    /**
     * The number of bytes needed to store a [NibbleArray].
     */
    private const val NIBBLE_ARRAY_LENGTH = 2048

    @Throws(IOException::class)
    fun readIntArray(inputStream: DataInput, count: Int): IntArray =
        inputStream.run {
            val array = IntArray(count)

            for (i in 0 until count) {
                array[i] = readInt()
            }

            array
        }

    @Throws(IOException::class)
    fun readByteArray(inputStream: DataInput, length: Int): ByteArray =
        inputStream.run { ByteArray(length).apply { readFully(this) } }

    /**
     * Reads and parses the chunk nibble array.
     *
     * @return the nibble array
     * @throws IOException if the bytes cannot be read
     * @see .NIBBLE_ARRAY_LENGTH
     */
    @Throws(IOException::class)
    fun readNibbleArray(dataInput: DataInput): NibbleArray =
        NibbleArray(
            readByteArray(dataInput, NIBBLE_ARRAY_LENGTH)
        )

    /**
     * Writes the next [.NIBBLE_ARRAY_LENGTH] bytes
     * to the specified nibble array.
     *
     * @param nibbleArray the nibble array to write to
     * @return the number of read bytes
     * @throws IOException if the bytes cannot be read
     */
    @Throws(IOException::class)
    fun readNibbleArray(dataInputStream: DataInputStream, nibbleArray: NibbleArray): Int =
        dataInputStream.read(
            nibbleArray.a()
        )

    @Throws(IOException::class)
    fun readBitSet(inputStream: DataInput, byteCount: Int): BitSet =
        BitSet.valueOf(
            readByteArray(inputStream, byteCount)
        )

    /**
     * Reads a block of zstd-compressed data. This method
     * expects the following ints to be the compressed size,
     * and uncompressed size respectively.
     *
     * @return the uncompressed data
     * @throws IOException if the bytes cannot be read
     * @throws IllegalArgumentException if the uncompressed length doesn't match
     */
    @Throws(IOException::class)
    fun readCompressed(inputStream: DataInput): ByteArray = inputStream.run {
        val compressedLength = readInt()
        val uncompressedLength = readInt()

        if (uncompressedLength != NIBBLE_ARRAY_LENGTH) {
            throw IllegalArgumentException("Uncompressed length doesn't match")
        }

        val compressed = ByteArray(compressedLength)

        readFully(compressed)

        return@run Zstd.decompress(compressed, uncompressedLength)
    }

    /**
     * Reads and parses a block of zstd-compressed bytes as
     * an NBT named compound tag.
     *
     * @return the parsed named compound tag.
     * @throws IOException if the bytes cannot be read
     * @see .readCompressed
     */
    @Throws(IOException::class)
    fun readCompressedCompound(inputStream: DataInput): NBTTagCompound = NBTCompressedStreamTools.a(
        ByteArrayInputStream(
            readCompressed(inputStream)
        )
    )

    /**
     * Skips a block of zstd-compressed data.
     *
     * @return the number of bytes skipped
     * @throws IOException if the bytes cannot be skipped
     * @see .readCompressed
     */
    @Throws(IOException::class)
    fun skipCompressed(inputStream: DataInput) =
        inputStream.skipBytes(
            inputStream.readInt() + 4
        )

}