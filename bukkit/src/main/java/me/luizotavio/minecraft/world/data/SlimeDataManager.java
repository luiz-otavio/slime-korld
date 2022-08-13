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

package me.luizotavio.minecraft.world.data;

import me.luizotavio.minecraft.common.SlimeWorld;
import me.luizotavio.minecraft.prototype.ProtoSlimeFile;
import me.luizotavio.minecraft.world.chunkloader.SlimeChunkLoader;
import net.minecraft.server.v1_8_R3.*;
import org.apache.commons.lang.ArrayUtils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author Luiz Otávio de Farias Corrêa
 * @since 13/08/2022
 */
public class SlimeDataManager extends WorldNBTStorage {

    private final UUID worldId = UUID.randomUUID();

    private final SlimeWorld world;
    private final SlimeChunkLoader chunkLoader;

    private WorldData worldData;

    // When unloading a world, Spigot tries to remove the region file from its cache.
    // To do so, it casts the world's IDataManager to a WorldNBTStorage, to be able
    // to use the getDirectory() method. Thanks to this, we have to create a custom
    // WorldNBTStorage with a fake file instead of just implementing the IDataManager interface
    //
    // Thanks Spigot!
    // Thanks SlimeWorldManager!
    public SlimeDataManager(SlimeWorld world, ProtoSlimeFile protoSlimeFile) {
        super(new File("temp_" + world.getName()), world.getName(), false);

        // The WorldNBTStorage automatically creates some files inside the base dir, so we have to delete them
        // (Thanks again Spigot)

        // Can't just access the baseDir field inside WorldNBTStorage cause it's private :P
        File baseDir = new File("temp_" + world.getName(), world.getName());
        new File(baseDir, "session.lock").delete();
        new File(baseDir, "data").delete();

        baseDir.delete();
        baseDir.getParentFile().delete();

        this.world = world;

        try {
            this.chunkLoader = new SlimeChunkLoader(
                protoSlimeFile.getProtoChunks()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public WorldData getWorldData() {
        if (worldData == null) {
            worldData = new SlimeWorldData(world);
        }

        return worldData;
    }

    @Override
    public void checkSession() throws ExceptionWorldConflict {}

    @Override
    public IChunkLoader createChunkLoader(WorldProvider worldProvider) {
        return this.chunkLoader;
    }

    @Override
    public void saveWorldData(WorldData worldData, NBTTagCompound nbtTagCompound) {}

    @Override
    public void saveWorldData(WorldData worldData) {}

    @Override
    public void a() {}

    @Override
    public File getDirectory() {
        return null;
    }

    @Override
    public void save(EntityHuman entityhuman) {}

    @Override
    public NBTTagCompound load(EntityHuman entityhuman) {
        return null;
    }

    @Override
    public String[] getSeenPlayers() {
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }

    @Override
    public File getDataFile(String s) {
        return null;
    }

    @Override
    public String g() {
        return null;
    }

    @Override
    public UUID getUUID() {
        return worldId;
    }
}
