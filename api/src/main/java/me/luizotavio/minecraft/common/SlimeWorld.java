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

package me.luizotavio.minecraft.common;

import me.luizotavio.minecraft.common.exception.InternalSlimeException;
import me.luizotavio.minecraft.common.service.SlimeKorld;
import me.luizotavio.minecraft.common.settings.SettingsProperty;
import me.luizotavio.minecraft.common.version.WorldVersion;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Representation of a slime world.
 * Slime world is an unique format for lightweight worlds.
 * Idea is to save the world in a compressed format with necessary metadata.
 * The format of each slime world must be the same as the format of the SLIME_FORMAT.
 *
 * @author Luiz Otávio de Farias Corrêa
 * @since 11/08/2022
 */
public interface SlimeWorld {

    /**
     * @return The name of the world.
     */
    String getName();

    /**
     * @return The delegator of the world.
     */
    SlimeKorld getKorld();

    /**
     * @return The bukkit instance of the world.
     */
    @Nullable
    World getBukkitWorld();

    /**
     * @return The version of the world.
     */
    @NotNull
    WorldVersion getVersion();

    /**
     * @return An immutable set of the settings of the world.
     */
    @NotNull
    Set<SettingsProperty<?>> getSettings();

    /**
     * Checks if the world has the given property, if not, returns false.
     * @param property The property to get the value of.
     * @return True if the world has the property, false otherwise.
     */
    boolean hasProperty(@NotNull SettingsProperty<?> property);

    /**
     * Update the value of the given property.
     * @param property The property to update.
     * @param value The new value of the property.
     * @param <T> The type of the property.
     */
    <T> void setProperty(@NotNull SettingsProperty<T> property, @NotNull T value);

    /**
     * @param property The property to get the value of.
     * @return The value of the property, if doesn't contains, returns default value.
     */
    @NotNull
    <T> T getProperty(SettingsProperty<T> property);

    /**
     * Reset the world if it's not reseted.
     * @return The reset world instance.
     * @throws InternalSlimeException If can't reset the world.
     */
    @Nullable
    World reset() throws InternalSlimeException;

    /**
     * Unload the world if it's not unloaded.
     * @throws InternalSlimeException If can't unload the world.
     */
    void unload() throws InternalSlimeException;

    /**
     * Initialize the world if it's not initialized.
     * @return The initialized world instance.
     * @throws InternalSlimeException If can't initialize the world.
     */
    @Nullable
    World initialize() throws InternalSlimeException;

}
