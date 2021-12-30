package io.github.luizotavio.slimekorld

import net.minecraft.server.v1_8_R3.Block
import net.minecraft.server.v1_8_R3.NibbleArray
import kotlin.experimental.and

/**
 * Thank you so much hugmanrique for this code!
 * https://github.com/hugmanrique/Slime
 */
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

}