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

package me.luizotavio.minecraft.strategy;

import me.luizotavio.minecraft.common.SlimeWorld;
import me.luizotavio.minecraft.common.data.registry.SlimeDataRegistry;
import me.luizotavio.minecraft.common.exception.InternalSlimeException;
import me.luizotavio.minecraft.common.strategy.SlimeLoaderStrategy;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Luiz Otávio de Farias Corrêa
 * @since 13/08/2022
 */
public class FileSlimeLoaderStrategy implements SlimeLoaderStrategy {

    private final Plugin plugin;
    private final File folder;

    public FileSlimeLoaderStrategy(Plugin plugin, File folder) {
        this.plugin = plugin;
        this.folder = folder;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public byte[] load(String name, boolean force) throws InternalSlimeException {
        File file = new File(folder, name + ".slime");

        if (!file.exists()) {
            throw new InternalSlimeException("Slime file not found: " + file.getAbsolutePath());
        }

        try (FileInputStream inputStream = new FileInputStream(file)) {
            if (inputStream.available() == 0) {
                throw new InternalSlimeException("Slime file is empty: " + file.getAbsolutePath());
            }

            byte[] bytes = new byte[inputStream.available()];

            int result = inputStream.read(bytes);

            if (result != bytes.length && !force) {
                throw new InternalSlimeException("Could not read all bytes from file: " + file.getAbsolutePath());
            }

            return bytes;
        } catch (Exception e) {
            throw new InternalSlimeException("Error reading slime file: " + file.getAbsolutePath(), e);
        }
    }

    @Override
    public void save(SlimeWorld slimeWorld, byte[] data) throws InternalSlimeException {
        File file = new File(folder, slimeWorld.getName() + ".slime");
        // Delete the file if it already exists
        if (file.exists()) {
            file.delete();
        }

        Path path = file.toPath();
        try {
            Files.createDirectories(file.getParentFile().toPath());
            Files.createFile(path);
        } catch (Exception e) {
            throw new InternalSlimeException("Error creating slime file: " + file.getAbsolutePath(), e);
        }

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(data);
        } catch (Exception e) {
            throw new InternalSlimeException("Error writing slime file: " + file.getAbsolutePath(), e);
        }
    }
}
