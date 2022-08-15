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

package me.luizotavio.minecraft.world;

import me.luizotavio.minecraft.codec.SlimeOutputStream;
import me.luizotavio.minecraft.common.SlimeWorld;
import me.luizotavio.minecraft.common.exception.InternalSlimeException;
import me.luizotavio.minecraft.common.service.SlimeKorld;
import me.luizotavio.minecraft.common.settings.factory.SettingsPropertyFactory;
import me.luizotavio.minecraft.common.strategy.SlimeLoaderStrategy;
import me.luizotavio.minecraft.prototype.ProtoSlimeFile;
import me.luizotavio.minecraft.world.chunkloader.SlimeChunkLoader;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;

/**
 * @author Luiz Otávio de Farias Corrêa
 * @since 13/08/2022
 */
public class CraftSlimeWorld extends WorldServer {

    private static final MinecraftServer MINECRAFT_SERVER = MinecraftServer.getServer();

    private final ProtoSlimeFile protoSlimeFile;
    private final SlimeWorld slimeWorld;

    private final Object lock = new Object();

    /**
     * Thanks for SlimeWorldManager for the idea
     */
    public CraftSlimeWorld(
        @NotNull SlimeWorld slimeWorld,
        @NotNull ProtoSlimeFile slimeFile,
        @NotNull IDataManager idatamanager,
        int i
    ) {
        super(
            MINECRAFT_SERVER,
            idatamanager,
            idatamanager.getWorldData(),
            i,
            MINECRAFT_SERVER.methodProfiler,
            World.Environment.NORMAL,
            null
        );

        this.slimeWorld = slimeWorld;
        this.protoSlimeFile = slimeFile;

        b();
        this.tracker = new EntityTracker(this);
        addIWorldAccess(new WorldManager(MINECRAFT_SERVER, this));

        worldData.setDifficulty(
            EnumDifficulty.getById(slimeWorld.getProperty(SettingsPropertyFactory.DIFFICULTY).getValue())
        );
        Integer[] spawn = slimeWorld.getProperty(SettingsPropertyFactory.SPAWN_LOCATION);

        worldData.setSpawn(new BlockPosition(spawn[0], spawn[1], spawn[2]));
        super.setSpawnFlags(
            slimeWorld.getProperty(SettingsPropertyFactory.HAS_MONSTERS),
            slimeWorld.getProperty(SettingsPropertyFactory.HAS_ANIMALS)
        );

        this.pvpMode = slimeWorld.getProperty(SettingsPropertyFactory.HAS_PVP);
    }

    @Override
    public void save(boolean flag, IProgressUpdate iprogressupdate) throws ExceptionWorldConflict {
        if (!slimeWorld.hasProperty(SettingsPropertyFactory.SHOULD_SAVE)) {
            return;
        }

        super.save(flag, iprogressupdate);

        synchronized (lock) {
            SlimeKorld slimeKorld = Bukkit.getServicesManager()
                .load(SlimeKorld.class);

            SlimeLoaderStrategy loaderStrategy = slimeKorld.getLoaderStrategy();

            if (loaderStrategy != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                try (SlimeOutputStream slimeOutputStream = new SlimeOutputStream(byteArrayOutputStream, slimeWorld, slimeKorld.getDataRegistry())) {
                    slimeOutputStream.write();
                } catch (Exception e) {
                    throw new ExceptionWorldConflict(e.getMessage());
                }

                try {
                    loaderStrategy.save(slimeWorld, byteArrayOutputStream.toByteArray());
                } catch (InternalSlimeException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    public ProtoSlimeFile getProtoSlimeFile() {
        return protoSlimeFile;
    }

    public SlimeWorld getSlimeWorld() {
        return slimeWorld;
    }
}
