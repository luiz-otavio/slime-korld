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

package me.luizotavio.minecraft.common.data.type;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTType;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

/**
 * @author Luiz Otávio de Farias Corrêa
 * @since 11/08/2022
 */
public enum SlimeDataType {
    STRING(new DataCodec<String>() {
        @Override
        public String decode(@NotNull String key, @NotNull NBTCompound inputStream) {
            return inputStream.getString(key);
        }

        @Override
        public void encode(@NotNull String key, @NotNull String value, @NotNull NBTCompound inputStream) {
            inputStream.setString(key, value);
        }
    }),
    BOOLEAN(new DataCodec<Boolean>() {
        @Override
        public Boolean decode(@NotNull String key, @NotNull NBTCompound inputStream) {
            return inputStream.getBoolean(key);
        }

        @Override
        public void encode(@NotNull String key, @NotNull Boolean value, @NotNull NBTCompound inputStream) {
            inputStream.setBoolean(key, value);
        }
    }),
    BYTE(new DataCodec<Byte>() {
        @Override
        public Byte decode(@NotNull String key, @NotNull NBTCompound inputStream) {
            return inputStream.getByte(key);
        }

        @Override
        public void encode(@NotNull String key, @NotNull Byte value, @NotNull NBTCompound inputStream) {
            inputStream.setByte(key, value);
        }
    }),
    SHORT(new DataCodec<Short>() {
        @Override
        public Short decode(@NotNull String key, @NotNull NBTCompound inputStream) {
            return inputStream.getShort(key);
        }

        @Override
        public void encode(@NotNull String key, @NotNull Short value, @NotNull NBTCompound inputStream) {
            inputStream.setShort(key, value);
        }
    }),
    INTEGER(new DataCodec<Integer>() {
        @Override
        public Integer decode(@NotNull String key, @NotNull NBTCompound inputStream) {
            return inputStream.getInteger(key);
        }

        @Override
        public void encode(@NotNull String key, @NotNull Integer value, @NotNull NBTCompound inputStream) {
            inputStream.setInteger(key, value);
        }
    }),
    LONG(new DataCodec<Long>() {
        @Override
        public Long decode(@NotNull String key, @NotNull NBTCompound inputStream) {
            return inputStream.getLong(key);
        }

        @Override
        public void encode(@NotNull String key, @NotNull Long value, @NotNull NBTCompound inputStream) {
            inputStream.setLong(key, value);
        }
    }),
    FLOAT(new DataCodec<Float>() {
        @Override
        public Float decode(@NotNull String key, @NotNull NBTCompound inputStream) {
            return inputStream.getFloat(key);
        }

        @Override
        public void encode(@NotNull String key, @NotNull Float value, @NotNull NBTCompound inputStream) {
            inputStream.setFloat(key, value);
        }
    }),
    DOUBLE(new DataCodec<Double>() {
        @Override
        public Double decode(@NotNull String key, @NotNull NBTCompound inputStream) {
            return inputStream.getDouble(key);
        }

        @Override
        public void encode(@NotNull String key, @NotNull Double value, @NotNull NBTCompound inputStream) {
            inputStream.setDouble(key, value);
        }
    }),
    CHAR(new DataCodec<Character>() {
        @Override
        public Character decode(@NotNull String key, @NotNull NBTCompound inputStream) {
            return inputStream.getString(key)
                .charAt(0);
        }

        @Override
        public void encode(@NotNull String key, @NotNull Character value, @NotNull NBTCompound inputStream) {
            inputStream.setString(key, value.toString());
        }
    }),
    BYTE_ARRAY(new DataCodec<byte[]>() {
        @Override
        public byte[] decode(@NotNull String key, @NotNull NBTCompound inputStream) {
            return inputStream.getByteArray(key);
        }

        @Override
        public void encode(@NotNull String key, @NotNull byte[] value, @NotNull NBTCompound inputStream) {
            inputStream.setByteArray(key, value);
        }
    }),
    COMPOUND(new DataCodec<NBTCompound>() {
        @Override
        public NBTCompound decode(@NotNull String key, @NotNull NBTCompound inputStream) {
            return inputStream.getCompound(key);
        }

        @Override
        public void encode(@NotNull String key, @NotNull NBTCompound value, @NotNull NBTCompound inputStream) {
            NBTCompound compound = inputStream.getOrCreateCompound(key);

            compound.mergeCompound(value);
        }
    });

    private static final EnumSet<NBTType> SUPPORTED_NBT_TYPES = EnumSet.of(
        NBTType.NBTTagCompound,
        NBTType.NBTTagString,
        NBTType.NBTTagInt,
        NBTType.NBTTagLong,
        NBTType.NBTTagFloat,
        NBTType.NBTTagDouble,
        NBTType.NBTTagIntArray
    );

    private final DataCodec<?> dataCodec;

    SlimeDataType(DataCodec<?> dataCodec) {
        this.dataCodec = dataCodec;
    }

    public DataCodec<?> getDataCodec() {
        return dataCodec;
    }

    public abstract static class DataCodec<T> {

        public abstract T decode(@NotNull String key, @NotNull NBTCompound inputStream);

        public abstract void encode(@NotNull String key, @NotNull T value, @NotNull NBTCompound inputStream);

    }
}
