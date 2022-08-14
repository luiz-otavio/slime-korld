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

package me.luizotavio.minecraft;

import com.google.common.collect.ImmutableSet;
import de.tr7zw.nbtapi.NBTContainer;
import me.luizotavio.minecraft.codec.SlimeInputStream;
import me.luizotavio.minecraft.common.SlimeWorld;
import me.luizotavio.minecraft.common.data.AbstractSlimeData;
import me.luizotavio.minecraft.common.data.registry.SlimeDataRegistry;
import me.luizotavio.minecraft.common.event.impl.SlimeWorldInitializeEvent;
import me.luizotavio.minecraft.common.event.impl.SlimeWorldResetEvent;
import me.luizotavio.minecraft.common.event.impl.SlimeWorldUnloadEvent;
import me.luizotavio.minecraft.common.exception.InternalSlimeException;
import me.luizotavio.minecraft.common.service.SlimeKorld;
import me.luizotavio.minecraft.common.settings.SettingsProperty;
import me.luizotavio.minecraft.common.strategy.SlimeLoaderStrategy;
import me.luizotavio.minecraft.common.version.WorldVersion;
import me.luizotavio.minecraft.data.container.BukkitSlimePersistentContainer;
import me.luizotavio.minecraft.prototype.ProtoSlimeFile;
import me.luizotavio.minecraft.world.CraftSlimeWorld;
import me.luizotavio.minecraft.world.data.SlimeDataManager;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * @author Luiz Otávio de Farias Corrêa
 * @since 13/08/2022
 */
public class BukkitSlimeWorld implements SlimeWorld {

    private static final MinecraftServer MINECRAFT_SERVER = MinecraftServer.getServer();

    private final String name;
    private final WorldVersion worldVersion;

    private final Map<String, SettingsProperty<?>> properties = new Hashtable<>();

    public BukkitSlimeWorld(
        @NotNull String name,
        @NotNull WorldVersion worldVersion,
        SettingsProperty<?>... properties
    ) {
        this.name = name;
        this.worldVersion = worldVersion;

        for (SettingsProperty property : properties) {
            setProperty(property, property.getDefaultValue());
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public SlimeKorld getKorld() {
        return Bukkit.getServicesManager().load(SlimeKorld.class);
    }

    @Override
    public @Nullable World getBukkitWorld() {
        return Bukkit.getWorld(name);
    }

    @Override
    public @NotNull WorldVersion getVersion() {
        return worldVersion;
    }

    @Override
    public @NotNull Set<SettingsProperty<?>> getSettings() {
        return ImmutableSet.copyOf(properties.values());
    }

    @Override
    public boolean hasProperty(@NotNull SettingsProperty<?> property) {
        return properties.containsKey(property.getName());
    }

    @Override
    public <T> void setProperty(@NotNull SettingsProperty<T> property, @NotNull T value) {
        properties.put(property.getName(), property.clone());
    }

    @Override
    public <T> @NotNull T getProperty(SettingsProperty<T> property) {
        if (!hasProperty(property)) {
            return property.getDefaultValue();
        }

        SettingsProperty<T> target = (SettingsProperty<T>) properties.get(property.getName());

        return target.getValue();
    }

    @Override
    public @Nullable World reset() throws InternalSlimeException {
        World world = getBukkitWorld();

        if (world == null) {
            throw new InternalSlimeException("World not found");
        }

        SlimeWorldResetEvent event = new SlimeWorldResetEvent(this, world)
            .call();

        if (event.isCancelled()) {
            throw new InternalSlimeException("World reset cancelled");
        }

        Bukkit.unloadWorld(world, false);

        return initialize();
    }

    @Override
    public void unload() throws InternalSlimeException {
        World world = getBukkitWorld();

        if (world == null) {
            throw new InternalSlimeException("World not found");
        }

        SlimeWorldUnloadEvent event = new SlimeWorldUnloadEvent(this)
            .call();

        if (event.isCancelled()) {
            throw new InternalSlimeException("World unload cancelled");
        }

        Bukkit.unloadWorld(world, true);
    }

    @Override
    public @Nullable World initialize() throws InternalSlimeException {
        if (getBukkitWorld() != null) {
           throw new InternalSlimeException("World already exists");
        }

        SlimeLoaderStrategy loader = getKorld()
            .getLoaderStrategy();

        byte[] data = loader.load(name, false);

        ProtoSlimeFile protoSlimeFile;

        try (SlimeInputStream stream = new SlimeInputStream(new ByteArrayInputStream(data))) {
            protoSlimeFile = stream.transform(worldVersion, getSettings());
        } catch (Exception e) {
            throw new InternalSlimeException(e);
        }

        SlimeDataManager dataManager = new SlimeDataManager(this, protoSlimeFile);

        int dimension = CraftWorld.CUSTOM_DIMENSION_OFFSET + MINECRAFT_SERVER.worlds.size();

        boolean isUsed = false;

        // Check if dimension is already in use
        do {
            for (WorldServer worldServer : MINECRAFT_SERVER.worlds) {
                isUsed = worldServer.dimension == dimension;

                if (isUsed) {
                    dimension++;
                    break;
                }
            }
        } while (isUsed);

        CraftSlimeWorld craftWorld = new CraftSlimeWorld(
            this,
            protoSlimeFile,
            dataManager,
            dimension
        );

        SlimeWorldInitializeEvent event = new SlimeWorldInitializeEvent(this, craftWorld.getWorld())
            .call();

        if (event.isCancelled()) {
            throw new InternalSlimeException("World initialization cancelled");
        }

        MINECRAFT_SERVER.worlds.add(craftWorld);
        MINECRAFT_SERVER.server.addWorld(craftWorld.getWorld());

        NBTTagCompound extraData = protoSlimeFile.getExtraData();

        if (extraData != null) {
            SlimeDataRegistry registry = getKorld()
                .getDataRegistry();

            BukkitSlimePersistentContainer persistentContainer = new BukkitSlimePersistentContainer(
                new NBTContainer(extraData)
            );

            for (AbstractSlimeData slimeData : registry.getRegistered()) {
                slimeData.deserialize(this, persistentContainer);
            }
        }

        // Fix world maps
        NBTTagCompound worldMaps = protoSlimeFile.getWorldMaps();
        NBTTagList maps = worldMaps.getList("maps", 10);

        for (int i = 0; i < maps.size(); i++) {
            NBTTagCompound map = maps.get(i);

            WorldMap worldMap = new WorldMap("map_" + map.getInt("id"));
            worldMap.a(map);

            craftWorld.worldMaps.a(worldMap.id, worldMap);
        }

        return craftWorld.getWorld();
    }
}
