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

package me.luizotavio.minecraft.common.service;

import me.luizotavio.minecraft.common.data.registry.SlimeDataRegistry;
import me.luizotavio.minecraft.common.factory.SlimeWorldFactory;
import me.luizotavio.minecraft.common.strategy.SlimeLoaderStrategy;
import org.apache.logging.log4j.Logger;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * Service to be registered in services manager.
 * It should be used when that library is used as a dependency.
 *
 * @author Luiz Otávio de Farias Corrêa
 * @since 12/08/2022
 */
public interface SlimeKorld {

    /**
     * Retrieve the current holder of the service.
     * @return The plugin that is holding the service.
     */
    Plugin getHolder();

    /**
     * Retrieve the logger of the service.
     * @return The logger of the service.
     */
    Logger getLogger();

    /**
     * Retrieve the version of the service.
     * @return The version of the service.
     */

    String getVersion();

    /**
     * Retrieve the factory of the service.
     * @return The factory of the service.
     */

    SlimeWorldFactory getFactory();

    /**
     * Retrieve the data registry of the service.
     * @return The data registry of the service.
     */
    SlimeDataRegistry getDataRegistry();

    /**
     * Retrieve the loader strategy of the service.
     * @return The loader strategy of the service.
     */

    SlimeLoaderStrategy getLoaderStrategy();

    /**
     * Update the current loader strategy of the service.
     * @param loaderStrategy The new loader strategy.
     */
    void setLoaderStrategy(@NotNull SlimeLoaderStrategy loaderStrategy);

}
