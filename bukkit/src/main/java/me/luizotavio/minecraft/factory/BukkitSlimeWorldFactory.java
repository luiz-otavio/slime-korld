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

package me.luizotavio.minecraft.factory;

import me.luizotavio.minecraft.BukkitSlimeWorld;
import me.luizotavio.minecraft.codec.SlimeOutputStream;
import me.luizotavio.minecraft.common.SlimeWorld;
import me.luizotavio.minecraft.common.exception.InternalSlimeException;
import me.luizotavio.minecraft.common.factory.SlimeWorldFactory;
import me.luizotavio.minecraft.common.service.SlimeKorld;
import me.luizotavio.minecraft.common.settings.SettingsProperty;
import me.luizotavio.minecraft.common.util.Pair;
import me.luizotavio.minecraft.common.version.WorldVersion;
import me.luizotavio.minecraft.world.CraftSlimeWorld;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;

/**
 * @author Luiz Otávio de Farias Corrêa
 * @since 12/08/2022
 */
public class BukkitSlimeWorldFactory implements SlimeWorldFactory {

    private final SlimeKorld slimeKorld;

    public BukkitSlimeWorldFactory(@NotNull SlimeKorld slimeKorld) {
        this.slimeKorld = slimeKorld;
    }

    @Override
    public @Nullable SlimeWorld createWorld(@NotNull String name, @NotNull WorldVersion worldVersion, SettingsProperty<?>... properties) throws InternalSlimeException {
        World world = Bukkit.getWorld(name);

        if (world != null) {
            SlimeWorld slimeWorld = getSlimeWorld(world);

            if (slimeWorld != null) {
                return slimeWorld;
            }
        }

        return new BukkitSlimeWorld(name, worldVersion, properties);
    }

    @Override
    public @NotNull SlimeWorld createWorld(@NotNull World world, @NotNull WorldVersion worldVersion, SettingsProperty<?>... properties) throws InternalSlimeException {
        SlimeWorld slimeWorld = getSlimeWorld(world);

        if (slimeWorld != null) {
            return slimeWorld;
        }

        return new BukkitSlimeWorld(world.getName(), worldVersion, properties);
    }

    @Override
    public @Nullable SlimeWorld getSlimeWorld(@NotNull World world) {
        WorldServer worldServer = ((CraftWorld) world).getHandle();

        if (worldServer instanceof CraftSlimeWorld) {
            CraftSlimeWorld craftSlimeWorld = (CraftSlimeWorld) worldServer;

            if (craftSlimeWorld.getSlimeWorld() != null) {
                return craftSlimeWorld.getSlimeWorld();
            }
        }

        return null;
    }

    @Override
    public @NotNull Pair<SlimeWorld, byte[]> transform(@NotNull World world) throws InternalSlimeException {
        SlimeWorld slimeWorld = getSlimeWorld(world);

        if (slimeWorld != null) {
            byte[] bytes = slimeKorld.getLoaderStrategy()
                .load(slimeWorld.getName(), true);

            return new Pair<>(slimeWorld, bytes);
        }

        BukkitSlimeWorld bukkitSlimeWorld = new BukkitSlimeWorld(world.getName(), WorldVersion.V1_8_R3);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try (SlimeOutputStream slimeOutputStream = new SlimeOutputStream(byteArrayOutputStream, bukkitSlimeWorld, slimeKorld.getDataRegistry())) {
            slimeOutputStream.write();
        } catch (Exception e) {
            throw new InternalSlimeException(e);
        }

        return new Pair<>(bukkitSlimeWorld, byteArrayOutputStream.toByteArray());
    }
}
