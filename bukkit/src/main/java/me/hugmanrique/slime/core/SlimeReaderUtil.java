/*
 * MIT License
 *
 * Copyright (c) [2022] [LUIZ O. F. CORRÊA]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.hugmanrique.slime.core;

import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.NibbleArray;

/**
 * Provides utilities to manage data in a Slime stream. (Thanks for hugmanrique!)
 */
public final class SlimeReaderUtil {

    private SlimeReaderUtil() {
        throw new AssertionError();
    }

    /**
     * Gets the block array index of the block specified
     * by the chunk section coordinates.
     *
     * @param x the chunk section x-coordinate
     * @param y the chunk section y-coordinate
     * @param z the chunk section z-coordinate
     * @return the index
     */
    public static int getBlockIndex(int x, int y, int z) {
        return y << 8 | z << 4 | x;
    }

    /**
     * Converts the specified block and block data arrays to
     * internal block IDs.
     *
     * @param blockIds the array to write the ids to
     * @param blocks the block array
     * @param data the block data array
     */
    public static void readBlockIds(final char[] blockIds, final byte[] blocks, final NibbleArray data) {
        for (int i = 0; i < blockIds.length; i++) {
            int x = i & 0xF;
            int y = i >> 8 & 0xF;
            int z = i >> 4 & 0xF;

            int id = blocks[i] & 0xFF;
            int blockData = data.a(x, y, z);

            blockIds[i] = getBlockId(id, blockData);
        }
    }

    public static char getBlockId(int id, int blockData) {
        int packed = id << 4 | blockData;

        if (Block.d.a(packed) == null) {
            // Convert old block
            Block block = Block.getById(id);

            if (block != null) {
                try {
                    blockData = block.toLegacyData(block.fromLegacyData(blockData));
                } catch (Exception ignored) {
                    blockData = block.toLegacyData(block.getBlockData());
                }

                // Recompute packed ID
                packed = id << 4 | blockData;
            }
        }

        return (char) packed;
    }
}
