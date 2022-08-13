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

package me.luizotavio.minecraft.common.version;

/**
 * Contains all formats of the slime version of this library.
 * Almost values should be updated as long the format of the version is the same.
 *
 * @author Luiz Otávio de Farias Corrêa
 * @since 11/08/2022
 */
public enum WorldVersion {
    V1_8_R3(0x01),
    V1_9_R1(0x02),
    V1_11_R1(0x03),
    V1_13_R1(0x04),
    V1_13_R2(0x05);

    private final byte version;

    WorldVersion(int version) {
        this.version = (byte) version;
    }

    public byte getByteVersion() {
        return version;
    }

    public static WorldVersion fromByte(byte version) {
        for (WorldVersion worldVersion : values()) {
            if (worldVersion.version == version) {
                return worldVersion;
            }
        }
        return null;
    }

}
