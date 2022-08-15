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

package me.luizotavio.minecraft.plugin;

import me.luizotavio.minecraft.BukkitSlimeKorld;
import me.luizotavio.minecraft.common.SlimeWorld;
import me.luizotavio.minecraft.common.service.SlimeKorld;
import me.luizotavio.minecraft.common.version.WorldVersion;
import me.luizotavio.minecraft.plugin.command.SlimeCommand;
import me.luizotavio.minecraft.plugin.listener.SlimeHandler;
import me.saiintbrisson.bukkit.command.BukkitFrame;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Luiz Otávio de Farias Corrêa
 * @since 13/08/2022
 */
public class SlimePlugin extends JavaPlugin {

    private File worldFolder;

    @Override
    public void onLoad() {
        worldFolder = new File(Bukkit.getWorldContainer(), "world-slime-test");

        // Copy the folder to the world folder
        if (!worldFolder.exists()) {
            InputStream inputStream = getResource("world-slime-test.zip");

            if (inputStream == null) {
                throw new RuntimeException("Could not find world-slime-test folder");
            }

            try {
                // Unzip the folder
                try (ZipInputStream stream = new ZipInputStream(inputStream)) {
                    ZipEntry zipEntry;

                    byte[] bytes = new byte[1024];

                    while ((zipEntry = stream.getNextEntry()) != null) {
                        File file = new File(Bukkit.getWorldContainer(), zipEntry.getName());

                        if (zipEntry.isDirectory()) {
                            file.mkdirs();
                        } else {
                            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                                int length;

                                while ((length = stream.read(bytes)) > 0) {
                                    outputStream.write(bytes, 0, length);
                                }
                            }
                        }
                    }
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Bukkit.getLogger().info("World folder created");
        }
    }

    @Override
    public void onEnable() {
        SlimeKorld slimeKorld = BukkitSlimeKorld.createInstance(this);

        World world = null;

        boolean exists;
        try {
            SlimeWorld slimeData = slimeKorld.getFactory()
                .createWorld("world-slime-test", WorldVersion.V1_8_R3);

            world = slimeData.initialize();

            exists = true;

            Bukkit.getLogger().info("World found");
        } catch (Exception exception) {
            exists = false;
        }

        if (!exists) {
            world = new WorldCreator("world-slime-test")
                .createWorld();
        }

        BukkitFrame bukkitFrame = new BukkitFrame(this);
        bukkitFrame.registerCommands(new SlimeCommand());

        getServer().getPluginManager().registerEvents(new SlimeHandler(world), this);

        Bukkit.getLogger().info("Plugin enabled");
    }

    @Override
    public void onDisable() {
    }
}
