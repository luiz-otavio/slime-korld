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

package me.luizotavio.minecraft.common.settings.factory;

import me.luizotavio.minecraft.common.settings.SettingsProperty;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;

/**
 * Contains all default properties for each slime world.
 * Remember, that the default properties are the same for all worlds.
 *
 * @author Luiz Otávio de Farias Corrêa
 * @since 12/08/2022
 */
public class SettingsPropertyFactory {

    public static final SettingsProperty<Boolean> HAS_ENTITIES = SettingsProperty.createBooleanProperty("hasEntities", false, false);
    public static final SettingsProperty<Boolean> HAS_EXTRA_DATA = SettingsProperty.createBooleanProperty("hasExtraData", false, false);
    public static final SettingsProperty<Boolean> HAS_PVP = SettingsProperty.createBooleanProperty("hasPVP", false, false);
    public static final SettingsProperty<Boolean> HAS_MONSTERS = SettingsProperty.createBooleanProperty("hasMonsters", false, true);
    public static final SettingsProperty<Boolean> HAS_ANIMALS = SettingsProperty.createBooleanProperty("hasAnimals", false, true);
    public static final SettingsProperty<Boolean> SHOULD_SAVE = SettingsProperty.createBooleanProperty("shouldSave", false, false);

    public static final SettingsProperty<Difficulty> DIFFICULTY = SettingsProperty.createEnumProperty("difficulty", Difficulty.NORMAL, Difficulty.NORMAL, Difficulty.class);
    public static final SettingsProperty<GameMode> GAMEMODE = SettingsProperty.createEnumProperty("gamemode", GameMode.SURVIVAL, GameMode.SURVIVAL, GameMode.class);

    public static final SettingsProperty<Integer[]> SPAWN_LOCATION = SettingsProperty.createArrayProperty(
        "spawnLocation",
        new Integer[] { 0, 50, 0 },
        new Integer[] { 0, 50, 0 },
        Integer.class
    );
}
