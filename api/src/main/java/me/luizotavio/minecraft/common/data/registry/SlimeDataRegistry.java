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

package me.luizotavio.minecraft.common.data.registry;

import me.luizotavio.minecraft.common.data.AbstractSlimeData;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Registry for extra-data in slime worlds.
 *
 * @author Luiz Otávio de Farias Corrêa
 * @since 12/08/2022
 */
public interface SlimeDataRegistry {

    /**
     * Registers a new data.
     *
     * @param data The data to register.
     */
    void registerOne(@NotNull AbstractSlimeData data);

    /**
     * Registers a collection of new data.
     *
     * @param data The data to register.
     */
    void registerAll(@NotNull AbstractSlimeData... data);

    /**
     * Unregisters a data.
     *
     * @param data The data to unregister.
     */
    void unregisterOne(@NotNull AbstractSlimeData data);

    /**
     * Unregisters a collection of data.
     *
     * @param data The data to unregister.
     */
    void unregisterAll(@NotNull AbstractSlimeData... data);

    /**
     * Unregister all data.
     */
    void unregisterAll();

    /**
     * Unregister all data of a plugin.
     *
     * @param plugin The plugin of the data.
     */
    void unregisterAll(@NotNull Plugin plugin);

    /**
     * Check if a data is registered.
     * @param name The name of the data.
     * @return True if the data is registered, false otherwise.
     */
    boolean isRegistered(@NotNull String name);

    /**
     * Retrieve the collection of registered data.
     * @return The collection of registered data.
     */
    @NotNull Collection<AbstractSlimeData> getRegistered();

}
