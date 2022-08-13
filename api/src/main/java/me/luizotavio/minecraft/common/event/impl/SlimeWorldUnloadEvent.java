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

package me.luizotavio.minecraft.common.event.impl;

import me.luizotavio.minecraft.common.SlimeWorld;
import me.luizotavio.minecraft.common.event.SlimeEvent;

/**
 * Bukkit event that is called when a slime world is unload.
 *
 * @author Luiz Otávio de Farias Corrêa
 * @since 12/08/2022
 */
public class SlimeWorldUnloadEvent extends SlimeEvent {

    private final SlimeWorld slimeWorld;

    public SlimeWorldUnloadEvent(SlimeWorld slimeWorld) {
        this.slimeWorld = slimeWorld;
    }

    /**
     * Gets the slime world of the event.
     * @return The slime world of the event.
     */
    public SlimeWorld getSlimeWorld() {
        return slimeWorld;
    }

}
