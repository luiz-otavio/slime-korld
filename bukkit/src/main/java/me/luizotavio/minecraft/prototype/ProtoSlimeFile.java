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

package me.luizotavio.minecraft.prototype;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import gnu.trove.map.hash.TLongObjectHashMap;
import me.hugmanrique.slime.core.SlimeReaderUtil;
import me.luizotavio.minecraft.common.version.WorldVersion;
import me.luizotavio.minecraft.prototype.chunk.ProtoSlimeChunk;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.util.LongHash;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.BitSet;

import static me.luizotavio.minecraft.codec.SlimeOutputStream.*;

/**
 * @author Luiz Otávio de Farias Corrêa
 * @since 13/08/2022
 */
public class ProtoSlimeFile {

    private final WorldVersion worldVersion;

    private final int width, depth, minX, minZ;

    private final BitSet chunks;

    private final byte[] chunkData, tileData, mapData;
    private final byte[] entityData, extraData;

    public ProtoSlimeFile(
        @NotNull WorldVersion worldVersion,
        int width,
        int depth,
        int minX,
        int minZ,
        @NotNull BitSet chunks,
        byte[] chunkData,
        byte[] tileData,
        byte[] mapData,
        byte[] entityData,
        byte[] extraData
    ) {
        this.worldVersion = worldVersion;
        this.width = width;
        this.depth = depth;
        this.minX = minX;
        this.minZ = minZ;
        this.chunks = chunks;
        this.chunkData = chunkData;
        this.tileData = tileData;
        this.mapData = mapData;
        this.entityData = entityData;
        this.extraData = extraData;
    }

    /**
     * Thanks for SlimeWorldManager && hugmanrique!
     * Transform the file into a {@link ProtoSlimeChunk} object.
     * @return The collection of chunks.
     * @throws IOException If an error occurs while reading the file.
     */
    public TLongObjectHashMap<ProtoSlimeChunk> getProtoChunks() throws IOException {
        NBTTagCompound entities = readCompound(entityData),
            tiles = readCompound(tileData);

        TLongObjectHashMap<ProtoSlimeChunk> protoChunks = new TLongObjectHashMap<>();

        ByteArrayDataInput input = ByteStreams.newDataInput(chunkData);

        for (int index = 0; index < chunks.size(); index++) {
            if (!chunks.get(index)) {
                continue;
            }

            int x = index % width + minX,
                z = index / width + minZ;

            int[] heightMap = new int[HEIGHTMAP_SIZE];

            for (int i = 0; i < HEIGHTMAP_SIZE; i++) {
                heightMap[i] = input.readInt();
            }

            byte[] biomeData = new byte[BIOME_SIZE];

            input.readFully(biomeData);

            byte[] sections = new byte[SECTION_PER_CHUNK >> 3];

            input.readFully(sections);

            BitSet populatedSections = BitSet.valueOf(sections);
            ChunkSection[] chunkSections = new ChunkSection[SECTION_PER_CHUNK];

            for (int sectionIndex = 0; sectionIndex != SECTION_PER_CHUNK; sectionIndex++) {
                if (!populatedSections.get(sectionIndex)) {
                    continue;
                }

                byte[] blockLight = new byte[NIBBLE_ARRAY_SIZE];

                // Has block light
                if (input.readBoolean()) {
                    input.readFully(blockLight);
                }

                byte[] blockLength = new byte[BLOCK_DATA_SIZE];

                input.readFully(blockLength);

                byte[] blockData = new byte[NIBBLE_ARRAY_SIZE];

                input.readFully(blockData);

                byte[] skyLight = new byte[NIBBLE_ARRAY_SIZE];

                // Has sky light
                if (input.readBoolean()) {
                    input.readFully(skyLight);
                }

                ChunkSection chunkSection = new ChunkSection(sectionIndex, true);

                chunkSection.a(
                    new NibbleArray(blockLight)
                );
                chunkSection.b(
                    new NibbleArray(skyLight)
                );

                SlimeReaderUtil.readBlockIds(chunkSection.getIdArray(), blockLength, new NibbleArray(blockData));

                chunkSection.recalcBlockCounts();

                chunkSections[sectionIndex] = chunkSection;
            }

            ProtoSlimeChunk protoChunk = new ProtoSlimeChunk(
                x,
                z,
                chunkSections,
                heightMap,
                biomeData
            );

            protoChunks.put(LongHash.toLong(x, z), protoChunk);
        }

        NBTTagList entitiesList = entities.getList("entities", 10),
            tilesList = tiles.getList("tiles", 10);

        if (!entitiesList.isEmpty()) {
            for (int index = 0; index < entitiesList.size(); index++) {
                NBTTagCompound entity = entitiesList.get(index);

                NBTTagList position = entity.getList("Pos", 6);

                int x = (int) position.d(0),
                    z = (int) position.d(1);

                ProtoSlimeChunk protoChunk = protoChunks.get(LongHash.toLong(x >> 4, z >> 4));

                if (protoChunk != null) {
                    protoChunk.addEntity(entity);
                }
            }
        }

        if(!tilesList.isEmpty()) {
            for (int index = 0; index < tilesList.size(); index++) {
                NBTTagCompound tile = tilesList.get(index);

                int x = tile.getInt("x"),
                    z = tile.getInt("z");

                ProtoSlimeChunk protoChunk = protoChunks.get(LongHash.toLong(x >> 4, z >> 4));

                if (protoChunk != null) {
                    protoChunk.addTileEntity(tile);
                }
            }
        }

        return protoChunks;
    }

    public byte[] getTileData() {
        return tileData;
    }

    public byte[] getEntityData() {
        return entityData;
    }

    public byte[] getChunkData() {
        return chunkData;
    }

    public BitSet getChunks() {
        return chunks;
    }

    public int getMinX() {
        return minX;
    }

    public int getMinZ() {
        return minZ;
    }

    public int getDepth() {
        return depth;
    }

    public int getWidth() {
        return width;
    }

    public WorldVersion getWorldVersion() {
        return worldVersion;
    }

    @NotNull
    public NBTTagCompound getWorldMaps() {
        try {
            return readCompound(mapData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public NBTTagCompound getExtraData() {
        if (mapData == null) {
            return null;
        }

        try {
            return readCompound(extraData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private NBTTagCompound readCompound(byte[] src) throws IOException {
        return NBTCompressedStreamTools.a(ByteStreams.newDataInput(src), NBTReadLimiter.a);
    }

}
