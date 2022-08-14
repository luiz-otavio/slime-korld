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

package me.luizotavio.minecraft.common.factory;

import me.luizotavio.minecraft.common.SlimeWorld;
import me.luizotavio.minecraft.common.exception.InternalSlimeException;
import me.luizotavio.minecraft.common.service.SlimeKorld;
import me.luizotavio.minecraft.common.settings.SettingsProperty;
import me.luizotavio.minecraft.common.strategy.SlimeLoaderStrategy;
import me.luizotavio.minecraft.common.util.Pair;
import me.luizotavio.minecraft.common.version.WorldVersion;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * Factory for creating a slime world.
 *
 * @author Luiz Otávio de Farias Corrêa
 * @since 12/08/2022
 */
public interface SlimeWorldFactory {
    /**
     * Creates a new slime world based on {@link SlimeLoaderStrategy} from {@link SlimeKorld}}
     * Remember, after creating a new slime world, you must call {@link SlimeWorld#initialize()} to initialize the world.
     * @param name The name of the world.
     * @param worldVersion The version of the world.
     * @param properties The properties of the world.
     * @return The new slime world.
     * @throws InternalSlimeException If an error occurs.
     */
    SlimeWorld createWorld(@NotNull String name, @NotNull WorldVersion worldVersion, SettingsProperty<?> ... properties) throws InternalSlimeException;

    /**
     * Creates a new slime world based on a bukkit world.
     * Remember, you should not call {@link SlimeWorld#initialize()} after creating a new slime world because it's already initialized from bukkit world.
     * @param world The bukkit world.
     * @param worldVersion The version of the world.
     * @param properties The properties of the world.
     * @return The new slime world.
     * @throws InternalSlimeException If an error occurs.
     */
    @NotNull
    SlimeWorld createWorld(@NotNull World world, @NotNull  WorldVersion worldVersion, SettingsProperty<?> ... properties) throws InternalSlimeException;

    /**
     * Retrieve the slime world from a bukkit world.
     * @param world The bukkit world.
     * @return The slime world or null if it's not parent of a slime world.
     */
    @Nullable
    SlimeWorld getSlimeWorld(@NotNull World world);

    /**
     * Transform the bukkit world into a slime world.
     * Remember, that will not call {@link SlimeLoaderStrategy#save(SlimeWorld, byte[])} to save the world.
     * @param world The bukkit world.
     * @return The slime world.
     * @throws InternalSlimeException If an error occurs.
     */
    @NotNull
    Pair<SlimeWorld, byte[]> transform(@NotNull World world) throws InternalSlimeException;

}
