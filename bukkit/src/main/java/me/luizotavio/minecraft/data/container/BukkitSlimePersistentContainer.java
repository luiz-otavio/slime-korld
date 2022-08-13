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

package me.luizotavio.minecraft.data.container;

import de.tr7zw.nbtapi.NBTContainer;
import me.luizotavio.minecraft.common.data.container.SlimePersistentContainer;
import me.luizotavio.minecraft.common.data.type.SlimeDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author Luiz Otávio de Farias Corrêa
 * @since 12/08/2022
 */
public class BukkitSlimePersistentContainer implements SlimePersistentContainer {

    private final NBTContainer container;

    public BukkitSlimePersistentContainer(@NotNull final NBTContainer nbtContainer) {
        this.container = nbtContainer;
    }

    @Override
    public void set(@NotNull SlimeDataType slimeDataType, @NotNull String key, @Nullable Object value) {
        if (value == null) {
            container.removeKey(key);
            return;
        }

        SlimeDataType.DataCodec codec = slimeDataType.getDataCodec();

        codec.encode(key, value, container);
    }

    @Override
    public <T> @Nullable T get(@NotNull SlimeDataType slimeDataType, @NotNull String key) {
        SlimeDataType.DataCodec codec = slimeDataType.getDataCodec();

        return (T) codec.decode(key, container);
    }

    @Override
    public boolean contains(@NotNull SlimeDataType slimeDataType, @NotNull String key) {
        return container.hasKey(key);
    }

    @Override
    public void remove(@NotNull SlimeDataType slimeDataType, @NotNull String key) {
        container.removeKey(key);
    }

    @Override
    public <T> @Nullable T getOrDefault(@NotNull SlimeDataType slimeDataType, @NotNull String key, @NotNull Object defaultValue) {
        if (!contains(slimeDataType, key)) {
            return (T) defaultValue;
        }

        return get(slimeDataType, key);
    }
}
