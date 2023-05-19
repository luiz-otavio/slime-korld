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

package me.luizotavio.minecraft.prototype.chunk;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

/**
 * @author Luiz Otávio de Farias Corrêa
 * @since 13/08/2022
 */
public class ProtoSlimeChunk {

    private final int x, z;

    private final ChunkSection[] chunkSections;

    private final int[] heightMap;
    private final byte[] biomeIds;

    private final List<NBTTagCompound> tileEntities = new LinkedList<>();
    private final List<NBTTagCompound> entities = new LinkedList<>();

    public ProtoSlimeChunk(
        int x,
        int z,
        ChunkSection[] chunkSections,
        int[] heightMap,
        byte[] biomeIndex
    ) {
        this.x = x;
        this.z = z;
        this.chunkSections = chunkSections;
        this.heightMap = heightMap;
        this.biomeIds = biomeIndex;
    }

    public void addTileEntity(NBTTagCompound tileEntity) {
        tileEntities.add(tileEntity);
    }

    public void addEntity(NBTTagCompound entity) {
        entities.add(entity);
    }

    /**
     * Transform the proto chunk into a NMS Chunk.
     * Thanks for @hugmanrique for the idea.
     * @param world The world.
     * @return The NMS chunk.
     */
    @NotNull
    public Chunk toChunk(@NotNull World world) {
        Chunk chunk = new Chunk(world, x, z);

        chunk.a(heightMap); // HeightMap
        chunk.d(true); // TerrainPopulated
        chunk.e(true); // LightingPopulated
        chunk.c(0); // InhabitedTime
        chunk.a(chunkSections);
        chunk.a(biomeIds);

        for (NBTTagCompound tileEntity : tileEntities) {
            TileEntity entity = TileEntity.c(tileEntity);

            if (entity != null) {
                try {
                    chunk.a(entity);
                } catch (Exception exception) {
                    Bukkit.getLogger().log(Level.SEVERE, "Cannot put tile entity.");
                }
            }
        }

        for (NBTTagCompound entity : entities) {
            chunk.g(true);

            Entity targetEntity = EntityTypes.a(entity, world);

            if (targetEntity != null) {
                chunk.a(targetEntity);

                NBTTagCompound compound = entity.getCompound("Riding");

                if (compound == null) {
                    continue;
                }

                Entity ridingEntity = EntityTypes.a(compound, world);

                if (ridingEntity != null) {
                    chunk.a(ridingEntity);

                    targetEntity.mount(ridingEntity);
                }
            }
        }

        return chunk;
    }
}
