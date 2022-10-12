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

package me.luizotavio.minecraft.common.strategy;

import me.luizotavio.minecraft.common.SlimeWorld;
import me.luizotavio.minecraft.common.exception.InternalSlimeException;
import me.luizotavio.minecraft.common.service.SlimeKorld;
import org.bukkit.plugin.Plugin;

/**
 * Loader strategy to load/save slime world.
 * Unique instance for the delegator {@link SlimeKorld} to load/save the world.
 *
 * @author Luiz Otávio de Farias Corrêa
 * @since 13/08/2022
 */
public interface SlimeLoaderStrategy {

    /**
     * Retrieve the current holder of the loader strategy.
     * @return The plugin that is holding the loader strategy.
     */
    Plugin getPlugin();

    /**
     * Load and retrieve the compressed data of the world.
     * @param name The name of the world.
     * @param force If the world should be forced to be loaded.
     * @return The compressed data of the world.
     * @throws InternalSlimeException If the world is not found.
     */
    byte[] load(String name, boolean force) throws InternalSlimeException;

    /**
     * Save the compressed data of the world.
     * @param slimeWorld The slime world to be saved.
     * @param data The compressed data of the world.
     * @throws InternalSlimeException If the world is not found.
     */
    void save(SlimeWorld slimeWorld, byte[] data) throws InternalSlimeException;

}
