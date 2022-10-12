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

package me.luizotavio.minecraft.plugin.command;

import me.luizotavio.minecraft.common.SlimeWorld;
import me.luizotavio.minecraft.common.exception.InternalSlimeException;
import me.luizotavio.minecraft.common.service.SlimeKorld;
import me.luizotavio.minecraft.common.settings.factory.SettingsPropertyFactory;
import me.luizotavio.minecraft.common.util.Pair;
import me.luizotavio.minecraft.common.version.WorldVersion;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;

/**
 * @author Luiz Otávio de Farias Corrêa
 * @since 13/08/2022
 */
public class SlimeCommand {

    @Command(
        name = "slime",
        target = CommandTarget.PLAYER
    )
    public void handleSlimeCommand(Context<Player> context) {
        Player player = context.getSender();

        player.sendMessage(
            new String[]{
                "§aSlime world testing version: §e" + Bukkit.getVersion(),
                "§aType /slime convert - to convert your world to the new format",
                "§aType /slime save - to save your world",
                " "
            }
        );
    }

    @Command(
        name = "slime.convert"
    )
    public void handleSlimeConvertCommand(Context<Player> context) throws InternalSlimeException {
        Player player = context.getSender();

        player.sendMessage("§aConverting world to new format...");

        SlimeKorld slimeKorld = Bukkit.getServicesManager()
            .load(SlimeKorld.class);

        Pair<SlimeWorld, byte[]> pair = slimeKorld.getFactory()
            .transform(player.getWorld());

        // Save it to the new format
        slimeKorld.getLoaderStrategy().save(pair.getKey(), pair.getValue());

        player.sendMessage("§aWorld converted to new format");
    }

    @Command(
        name = "slime.save"
    )
    public void handleSlimeSaveCommand(Context<Player> context) throws InternalSlimeException {
        Player player = context.getSender();

        player.sendMessage("§aSaving world...");

        SlimeKorld slimeKorld = Bukkit.getServicesManager()
            .load(SlimeKorld.class);

        SlimeWorld slimeWorld  = slimeKorld.getFactory()
                .createWorld(
                    player.getWorld(),
                    WorldVersion.V1_8_R3
                );

        slimeWorld.setProperty(SettingsPropertyFactory.SHOULD_SAVE, true);
        slimeWorld.unload();

        player.sendMessage("§aWorld saved");
    }

}
