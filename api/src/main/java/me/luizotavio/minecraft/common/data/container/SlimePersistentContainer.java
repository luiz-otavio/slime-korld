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

package me.luizotavio.minecraft.common.data.container;

import me.luizotavio.minecraft.common.data.type.SlimeDataType;
import org.bukkit.Warning;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Luiz Otávio de Farias Corrêa
 * @since 11/08/2022
 */
public interface SlimePersistentContainer {

    /**
     * Insert the given data into the container.
     * @param slimeDataType The type of the data.
     * @param key The key of the data.
     * @param value The value of the data.
     */
    void set(@NotNull SlimeDataType slimeDataType, @NotNull String key, @Nullable Object value);

    /**
     * Gets the value of the given data.
     * @param slimeDataType The type of the data.
     * @param key The key of the data.
     * @return The value of the data.
     * @param <T> The type of the data.
     */
    @Warning(reason = "This method should return the same type as the one passed as parameter")
    @Nullable <T> T get(@NotNull SlimeDataType slimeDataType, @NotNull String key);

    /**
     * Checks if the given data is stored in the container.
     * @param slimeDataType The type of the data.
     * @param key The key of the data.
     * @return True if the data is stored in the container, false otherwise.
     */
    boolean contains(@NotNull SlimeDataType slimeDataType, @NotNull String key);

    /**
     * Removes the given data from the container.
     * @param slimeDataType The type of the data.
     * @param key The key of the data.
     */
    void remove(@NotNull SlimeDataType slimeDataType, @NotNull String key);

    /**
     * Gets or creates a new data container for the given type.
     * @param slimeDataType The type of the data.
     * @param key The key of the data.
     * @param defaultValue The default value of the data.
     * @return The data container.
     * @param <T> The type of the data.
     */
    @Nullable <T> T getOrDefault(@NotNull SlimeDataType slimeDataType, @NotNull String key, @NotNull Object defaultValue);

}
