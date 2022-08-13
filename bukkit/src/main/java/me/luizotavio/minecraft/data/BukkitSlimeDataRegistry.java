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

package me.luizotavio.minecraft.data;

import com.google.common.collect.Sets;
import me.luizotavio.minecraft.common.data.AbstractSlimeData;
import me.luizotavio.minecraft.common.data.registry.SlimeDataRegistry;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * @author Luiz Otávio de Farias Corrêa
 * @since 12/08/2022
 */
public class BukkitSlimeDataRegistry implements SlimeDataRegistry {

    private final Set<AbstractSlimeData> abstractSlimeDatas = Sets.newConcurrentHashSet();

    @Override
    public void registerOne(@NotNull AbstractSlimeData data) {
        abstractSlimeDatas.add(data);
    }

    @Override
    public void registerAll(@NotNull AbstractSlimeData... data) {
        abstractSlimeDatas.addAll(
            Arrays.asList(data)
        );
    }

    @Override
    public void unregisterOne(@NotNull AbstractSlimeData data) {
        abstractSlimeDatas.remove(data);
    }

    @Override
    public void unregisterAll(@NotNull AbstractSlimeData... data) {
        Arrays.asList(data).forEach(
            abstractSlimeDatas::remove
        );
    }

    @Override
    public void unregisterAll() {
        abstractSlimeDatas.clear();
    }

    @Override
    public void unregisterAll(@NotNull Plugin plugin) {
        abstractSlimeDatas.removeIf(
            abstractSlimeData -> abstractSlimeData.getPlugin().equals(plugin)
        );
    }

    @Override
    public boolean isRegistered(@NotNull String name) {
        return abstractSlimeDatas.stream()
            .anyMatch(abstractSlimeData -> abstractSlimeData.getName().equals(name));
    }

    @Override
    public @NotNull Collection<AbstractSlimeData> getRegistered() {
        return Collections.unmodifiableSet(abstractSlimeDatas);
    }
}
