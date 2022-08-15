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
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.tr7zw.nbtapi.NBTContainer;
import me.luizotavio.minecraft.common.SlimeWorld;
import me.luizotavio.minecraft.common.data.AbstractSlimeData;
import me.luizotavio.minecraft.common.data.registry.SlimeDataRegistry;
import me.luizotavio.minecraft.common.exception.InternalSlimeException;
import me.luizotavio.minecraft.common.settings.factory.SettingsPropertyFactory;
import me.luizotavio.minecraft.common.util.Pair;
import me.luizotavio.minecraft.common.version.WorldVersion;
import me.luizotavio.minecraft.data.container.BukkitSlimePersistentContainer;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.BitSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static me.luizotavio.minecraft.common.version.SlimeVersion.CURRENT_SLIME_VERSION;
import static me.luizotavio.minecraft.common.version.SlimeVersion.SLIME_MAGIC_HEADER;

/**
 * @author Luiz Otávio de Farias Corrêa
 * @since 12/08/2022
 */
public class SlimeOutputStream extends DataOutputStream {

    public static final WorldVersion SUPPORTED_VERSION = WorldVersion.V1_8_R3;

    public static final int CHUNK_SIZE = 16384;
    public static final int NIBBLE_ARRAY_SIZE = 2048;
    public static final int BLOCK_DATA_SIZE = 4096;
    public static final int SECTION_PER_CHUNK = 16;
    public static final int HEIGHTMAP_SIZE = 256;
    public static final int BIOME_SIZE = 256;

    private final SlimeWorld slimeWorld;
    private final SlimeDataRegistry dataRegistry;

    /**
     * Creates a new data output stream to write data to the specified
     * underlying output stream. The counter <code>written</code> is
     * set to zero.
     *
     * @param out the underlying output stream, to be saved for later
     *            use.
     * @see FilterOutputStream#out
     */
    public SlimeOutputStream(@NotNull OutputStream out, @NotNull SlimeWorld slimeWorld, @NotNull SlimeDataRegistry dataRegistry) {
        super(out);
        this.slimeWorld = slimeWorld;
        this.dataRegistry = dataRegistry;
    }

    public void write() throws IOException, InternalSlimeException {
        write(SLIME_MAGIC_HEADER);
        write(CURRENT_SLIME_VERSION);

        byte version = slimeWorld.getVersion()
            .getByteVersion();

        write(version);

        World world = slimeWorld.getBukkitWorld();

        if (world == null) {
            throw new InternalSlimeException("World is null", null);
        }

        net.minecraft.server.v1_8_R3.WorldServer nmsWorld = ((CraftWorld) world).getHandle();

        // Fill all chunks and ordered by chunk X and Z. -- Took idea from SlimeWorldManager;
        List<Chunk> chunks = nmsWorld.chunkProviderServer.chunks
            .values()
            .stream()
            .sorted(Comparator.comparingLong(chunk -> (long) chunk.locZ * Integer.MAX_VALUE + chunk.locX))
            .collect(Collectors.toList());

        int minX = chunks.parallelStream()
            .mapToInt(chunk -> chunk.locX)
            .min()
            .orElse(0);

        int minZ = chunks.parallelStream()
            .mapToInt(chunk -> chunk.locZ)
            .min()
            .orElse(0);

        int maxX = chunks.parallelStream()
            .mapToInt(chunk -> chunk.locX)
            .max()
            .orElse(0);

        int maxZ = chunks.parallelStream()
            .mapToInt(chunk -> chunk.locZ)
            .max()
            .orElse(0);

        writeShort(minX); // - minX of where the chunks start
        writeShort(minZ); // - minZ of where the chunks start

        // Calc width and depth
        int width = maxX - minX + 1;
        int depth = maxZ - minZ + 1;

        writeShort(width); // - width of the chunks
        writeShort(depth); // - depth of the chunks

        // Make a bit set to know which chunks are loaded
        BitSet bitSet = new BitSet(width * depth);

        for (Chunk chunk : chunks) {
            bitSet.set((chunk.locZ - minZ) * width + (chunk.locX - minX) , true);
        }

        int chunkSize = (int) Math.ceil((width * depth) / 8.0D);

        fromBitSet(this, bitSet, chunkSize);

        // Ensure version instead of writing each chunk
        ensureVersion(slimeWorld);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(CHUNK_SIZE);

        try (DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {
            for (Chunk chunk : chunks) {
                int[] heightMap = chunk.heightMap;

                for (int index = 0; index < HEIGHTMAP_SIZE; index++) {
                    dataOutputStream.writeInt(heightMap[index]);
                }

                dataOutputStream.write(chunk.getBiomeIndex());
                ChunkSection[] chunkSections = chunk.getSections();
                BitSet sectionSet = new BitSet(SECTION_PER_CHUNK);

                for (int index = 0; index < SECTION_PER_CHUNK; index++) {
                    sectionSet.set(index, index < chunkSections.length && chunkSections[index] != null);
                }

                fromBitSet(dataOutputStream, sectionSet, 2);

                for (ChunkSection chunkSection : chunkSections) {
                    if (chunkSection == null) {
                        continue;
                    }

                    byte[] blockLight = chunkSection.getEmittedLightArray()
                        .a();

                    dataOutputStream.writeBoolean(blockLight != null && blockLight.length == NIBBLE_ARRAY_SIZE);

                    if (blockLight != null) {
                        dataOutputStream.write(blockLight);
                    }

                    Pair<byte[], NibbleArray> blockData = getBlocksId(chunkSection);

                    dataOutputStream.write(blockData.getKey());
                    dataOutputStream.write(blockData.getValue().a());

                    byte[] skyLight = chunkSection.getSkyLightArray()
                        .a();

                    dataOutputStream.writeBoolean(skyLight != null && skyLight.length == NIBBLE_ARRAY_SIZE);

                    if (skyLight != null) {
                        dataOutputStream.write(skyLight);
                    }
                }
            }
        }

        byte[] bytes = byteArrayOutputStream.toByteArray(),
            compressed = Zstd.compress(bytes);

        writeInt(compressed.length); // Compressed size
        writeInt(bytes.length); // Not compressed size

        write(compressed);

        // Write tile entities
        NBTTagList nbtTagList = new NBTTagList();

        for (Chunk chunk : chunks) {
            Collection<TileEntity> tileEntities = chunk.tileEntities
                .values();

            for (TileEntity tileEntity : tileEntities) {
                NBTTagCompound compound = new NBTTagCompound();

                tileEntity.b(compound);

                nbtTagList.add(compound);
            }
        }

        NBTTagCompound tileCompound = new NBTTagCompound();
        tileCompound.set("tiles", nbtTagList);

        ByteArrayDataOutput tileByteArrayOutputStream = ByteStreams.newDataOutput();
        // Write tile entities
        writeCompound(tileByteArrayOutputStream, tileCompound);

        byte[] tileBytes = tileByteArrayOutputStream.toByteArray(),
            tileCompressed = Zstd.compress(tileBytes);

        writeInt(tileCompressed.length); // Compressed size
        writeInt(tileBytes.length); // Not compressed size

        write(tileCompressed);

        // Fix the boolean of has entities
        if (slimeWorld.hasProperty(SettingsPropertyFactory.HAS_ENTITIES)) {
            writeBoolean(true);
            NBTTagList entityTagList = new NBTTagList();

            for (Chunk chunk : chunks) {
                for (List<Entity> entitySlice : chunk.getEntitySlices()) {
                    for (Entity entity : entitySlice) {
                        NBTTagCompound compound = new NBTTagCompound();

                        entity.e(compound);

                        entityTagList.add(compound);
                    }
                }
            }

            NBTTagCompound entityCompound = new NBTTagCompound();
            entityCompound.set("entities", entityTagList);

            ByteArrayDataOutput entityByteArrayOutputStream = ByteStreams.newDataOutput();
            // Write entities
            writeCompound(entityByteArrayOutputStream, entityCompound);

            byte[] entityBytes = entityByteArrayOutputStream.toByteArray(),
                entityCompressed = Zstd.compress(entityBytes);

            writeInt(entityCompressed.length); // Compressed size
            writeInt(entityBytes.length); // Not compressed size

            write(entityCompressed);
        } else {
            writeBoolean(false);
        }

        if (slimeWorld.hasProperty(SettingsPropertyFactory.HAS_EXTRA_DATA)) {
            NBTTagCompound compound = new NBTTagCompound();

            BukkitSlimePersistentContainer persistentContainer = new BukkitSlimePersistentContainer(
                new NBTContainer(compound)
            );

            for (AbstractSlimeData slimeData : dataRegistry.getRegistered()) {
                slimeData.serialize(slimeWorld, persistentContainer);
            }

            ByteArrayDataOutput extraByteArrayOutputStream = ByteStreams.newDataOutput();
            // Write extra data
            writeCompound(extraByteArrayOutputStream, compound);

            byte[] extraBytes = extraByteArrayOutputStream.toByteArray(),
                extraCompressed = Zstd.compress(extraBytes);

            writeInt(extraCompressed.length); // Compressed size
            writeInt(extraBytes.length); // Not compressed size

            write(extraCompressed);
        }

        NBTTagCompound mapCompound = new NBTTagCompound();
        NBTTagList mapTagList = new NBTTagList();

        PersistentCollection collection = nmsWorld.worldMaps;

        for (Object o : collection.c) {
            if (!(o instanceof WorldMap)) {
                continue;
            }

            WorldMap worldMap = (WorldMap) o;

            NBTTagCompound compound = new NBTTagCompound();

            worldMap.b(compound);
            // New id int tag
            compound.setInt("id", worldMap.centerX << 12 | worldMap.centerZ << 12 | worldMap.scale);

            mapTagList.add(compound);
        }

        mapCompound.set("maps", mapTagList);

        ByteArrayDataOutput mapByteArrayOutputStream = ByteStreams.newDataOutput();
        // Write map data
        writeCompound(mapByteArrayOutputStream, mapCompound);

        byte[] mapBytes = mapByteArrayOutputStream.toByteArray(),
            mapCompressed = Zstd.compress(mapBytes);

        writeInt(mapCompressed.length); // Compressed size
        writeInt(mapBytes.length); // Not compressed size

        write(mapCompressed);
    }

    private void writeCompound(@NotNull DataOutput dataOutput, @NotNull NBTTagCompound compound) throws IOException {
        NBTCompressedStreamTools.a(compound, dataOutput);
    }

    private void fromBitSet(@NotNull DataOutputStream stream, @NotNull BitSet bitSet, int chunkSize) throws IOException {
        byte[] bytes = bitSet.toByteArray();

        stream.write(bytes);

        int padding = chunkSize - bytes.length;

        for (int i = 0; i < padding; i++) {
            stream.write(0);
        }
    }

    private Pair<byte[], NibbleArray> getBlocksId(@NotNull ChunkSection chunkSection) {
        char[] blocksId = chunkSection.getIdArray();

        byte[] bytes = new byte[BLOCK_DATA_SIZE];
        NibbleArray nibbleArray = new NibbleArray();

        for (int index = 0; index < blocksId.length; index++) {
            char blockId = blocksId[index];

            bytes[index] = (byte) (blockId >> 4 & 0xFF);

            nibbleArray.a(index & 15, index >> 8 & 15, index >> 4 & 15, blockId & 15);
        }

        return new Pair<>(bytes, nibbleArray);
    }

    private void ensureVersion(@NotNull SlimeWorld slimeWorld) throws InternalSlimeException {
        if (slimeWorld.getVersion() != SUPPORTED_VERSION) {
            throw new InternalSlimeException("Unsupported version");
        }
    }
}
