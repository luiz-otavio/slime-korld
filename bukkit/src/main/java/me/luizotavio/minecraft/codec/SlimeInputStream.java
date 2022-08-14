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

package me.luizotavio.minecraft.codec;

import com.github.luben.zstd.Zstd;
import me.luizotavio.minecraft.common.exception.InternalSlimeException;
import me.luizotavio.minecraft.common.settings.SettingsProperty;
import me.luizotavio.minecraft.common.settings.factory.SettingsPropertyFactory;
import me.luizotavio.minecraft.common.version.WorldVersion;
import me.luizotavio.minecraft.prototype.ProtoSlimeFile;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Set;

import static me.luizotavio.minecraft.common.version.SlimeVersion.CURRENT_SLIME_VERSION;
import static me.luizotavio.minecraft.common.version.SlimeVersion.SLIME_MAGIC_HEADER;
import static org.apache.commons.lang.ArrayUtils.EMPTY_BYTE_ARRAY;

/**
 * @author Luiz Otávio de Farias Corrêa
 * @since 13/08/2022
 */
public class SlimeInputStream extends DataInputStream {

    /**
     * Creates a DataInputStream that uses the specified
     * underlying InputStream.
     *
     * @param in the specified input stream
     */
    public SlimeInputStream(@NotNull InputStream in) {
        super(in);
    }

    public ProtoSlimeFile transform(WorldVersion worldVersion, Set<SettingsProperty<?>> properties) throws IOException, InternalSlimeException {
        if (available() < 1) {
            throw new InternalSlimeException("No data available");
        }

        byte[] magic = new byte[SLIME_MAGIC_HEADER.length];

        int success = read(magic);

        if (success != magic.length) {
            throw new InternalSlimeException("Could not read magic header");
        }

        if (!Arrays.equals(SLIME_MAGIC_HEADER, magic)) {
            throw new InternalSlimeException("Invalid magic header");
        }

        // Fix slime version
        byte slimeVersion = readByte();

        if (slimeVersion != CURRENT_SLIME_VERSION) {
            throw new InternalSlimeException("That slime version isn't supported.");
        }

        byte version = readByte();

        if (version == 0) {
            throw new InternalSlimeException("Invalid version");
        }

        WorldVersion targetVersion = WorldVersion.fromByte(version);

        if (worldVersion != targetVersion) {
            throw new InternalSlimeException("Cannot convert from " + worldVersion + " to " + targetVersion);
        }

        int minX = readShort(),
            minZ = readShort();

        int depth = readShort(),
            width = readShort();

        if (depth < 0 || width < 0) {
            throw new InternalSlimeException("Invalid depth or width");
        }

        byte[] populatedChunks = new byte[(width * depth) >> 3];

        success = read(populatedChunks);

        if (success != populatedChunks.length) {
            throw new InternalSlimeException("Could not read populated chunks");
        }

        BitSet bitSet = BitSet.valueOf(populatedChunks);

        byte[] uncompressedChunks = readCompressed();
        byte[] uncompressedTiles = readCompressed();

        boolean hasEntities = readBoolean();

        byte[] uncompressedEntities;

        if (hasEntities && properties.contains(SettingsPropertyFactory.HAS_ENTITIES)) {
            uncompressedEntities = readCompressed();
        } else {
            uncompressedEntities = EMPTY_BYTE_ARRAY;
        }

        byte[] extraData = readCompressed(),
            mapData = null;

        boolean isExtra = available() > 0;

        if (isExtra) {
            mapData = readCompressed();
        }

        return new ProtoSlimeFile(
            worldVersion,
            width,
            depth,
            minX,
            minZ,
            bitSet,
            uncompressedChunks,
            uncompressedTiles,
            isExtra ? mapData : extraData,
            uncompressedEntities,
            isExtra ? extraData : null
        );
    }

    private byte[] readCompressed() throws IOException {
        int compressedSize = readInt(),
            uncompressedSize = readInt();

        byte[] compressed = new byte[compressedSize];

        int success = read(compressed);

        if (success != compressedSize) {
            throw new IOException("Could not read compressed data");
        }

        return Zstd.decompress(compressed, uncompressedSize);
    }
}
