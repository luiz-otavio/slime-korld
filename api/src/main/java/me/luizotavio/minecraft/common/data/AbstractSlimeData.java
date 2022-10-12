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

package me.luizotavio.minecraft.common.data;

import com.google.common.annotations.Beta;
import me.luizotavio.minecraft.common.SlimeWorld;
import me.luizotavio.minecraft.common.data.container.SlimePersistentContainer;
import org.bukkit.plugin.Plugin;

/**
 * Abstraction for storing extra data in a {@link SlimeWorld}.
 * It's currently in Beta, but you can use it.
 *
 * @author Luiz Otávio de Farias Corrêa
 * @since 11/08/2022
 */
@Beta
public abstract class AbstractSlimeData {

    private final Plugin plugin;
    private final String name;

    public AbstractSlimeData(Plugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;
    }

    /**
     * Gets the plugin of the data.
     * @return The plugin of the data.
     */
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * Gets the name of the data.
     * @return The name of the data.
     */
    public String getName() {
        return name;
    }

    /**
     * Called when the data is saved to the persistent container.
     * @param slimeWorld The slime world of the data.
     * @param persistentContainer The persistent container of the data.
     */
    public abstract void serialize(SlimeWorld slimeWorld, SlimePersistentContainer persistentContainer);

    /**
     * Called when the data is loaded from the persistent container.
     * @param slimeWorld The slime world of the data.
     * @param persistentContainer The persistent container of the data.
     */
    public abstract void deserialize(SlimeWorld slimeWorld, SlimePersistentContainer persistentContainer);

}
