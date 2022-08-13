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

import me.luizotavio.minecraft.common.data.registry.SlimeDataRegistry;
import me.luizotavio.minecraft.common.factory.SlimeWorldFactory;
import me.luizotavio.minecraft.common.service.SlimeKorld;
import me.luizotavio.minecraft.common.strategy.SlimeLoaderStrategy;
import me.luizotavio.minecraft.data.BukkitSlimeDataRegistry;
import me.luizotavio.minecraft.factory.BukkitSlimeWorldFactory;
import me.luizotavio.minecraft.strategy.FileSlimeLoaderStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

/**
 * @author Luiz Otávio de Farias Corrêa
 * @since 12/08/2022
 */
public class BukkitSlimeKorld implements SlimeKorld {

    private static final Logger SLIME_KORLD_LOGGER = LogManager.getLogger("SlimeKorld");

    public static SlimeKorld createInstance(@NotNull Plugin plugin) {
        Objects.requireNonNull(plugin, "plugin cannot be null");

        ServicesManager servicesManager = Bukkit.getServicesManager();

        if (servicesManager.isProvidedFor(SlimeKorld.class)) {
            SLIME_KORLD_LOGGER.info("SlimeKorld is already registered");

            return servicesManager.load(SlimeKorld.class);
        }

        SlimeKorld slimeKorld = new BukkitSlimeKorld(plugin);

        servicesManager.register(SlimeKorld.class, slimeKorld, plugin, ServicePriority.High);

        return slimeKorld;
    }

    private final Plugin plugin;

    private final SlimeWorldFactory slimeWorldFactory;
    private final SlimeDataRegistry slimeDataRegistry;

    private SlimeLoaderStrategy slimeLoaderStrategy;

    protected BukkitSlimeKorld(
        @NotNull Plugin plugin
    ) {
        this.plugin = Objects.requireNonNull(plugin, "plugin cannot be null");

        this.slimeWorldFactory = new BukkitSlimeWorldFactory(this);
        this.slimeDataRegistry = new BukkitSlimeDataRegistry();

        this.slimeLoaderStrategy = new FileSlimeLoaderStrategy(
            plugin,
            new File(Bukkit.getWorldContainer(), "slime")
        );
    }

    @Override
    public Plugin getHolder() {
        return plugin;
    }

    @Override
    public Logger getLogger() {
        return SLIME_KORLD_LOGGER;
    }

    @Override
    public String getVersion() {
        return Bukkit.getVersion();
    }

    @Override
    public SlimeWorldFactory getFactory() {
        return slimeWorldFactory;
    }

    @Override
    public SlimeDataRegistry getDataRegistry() {
        return slimeDataRegistry;
    }

    @Override
    public SlimeLoaderStrategy getLoaderStrategy() {
        return slimeLoaderStrategy;
    }

    @Override
    public void setLoaderStrategy(SlimeLoaderStrategy loaderStrategy) {
        this.slimeLoaderStrategy = loaderStrategy;
    }
}
