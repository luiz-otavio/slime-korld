package me.luizotavio.minecraft.util;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Luiz Otávio de Farias Corrêa
 * @since 08/01/2025
 */
public class Worlds {

    private static final Pattern REGION_PATTERN = Pattern.compile("r\\.([0-9-]+)\\.([0-9-]+)\\.mca");

    public static Set<Chunk> loadAllChunks(World world) {
        File worldDir = new File(Bukkit.getWorldContainer(), world.getName());
        File regionDir = new File(worldDir, "region");

        File[] regionFiles = regionDir.listFiles((dir, name) -> REGION_PATTERN.matcher(name).matches());

        Bukkit.getLogger()
            .info("Found " + (regionFiles.length * 1024) + " chunk candidates in " + regionFiles.length + " files to check for loading ...");

        Set<Chunk> loadedChunks = new HashSet<>();
        for (File f : regionFiles) {
            // extract coordinates from filename
            Matcher matcher = REGION_PATTERN.matcher(f.getName());
            if (!matcher.matches()) {
                Bukkit.getLogger().warning("FilenameFilter accepted unmatched filename: " + f.getName());
                continue;
            }

            int mcaX = Integer.parseInt(matcher.group(1));
            int mcaZ = Integer.parseInt(matcher.group(2));

            int loadedCount = 0;

            for (int cx = 0; cx < 32; cx++) {
                for (int cz = 0; cz < 32; cz++) {
                    int chunkX = (mcaX << 5) + cx,
                        chunkZ = (mcaZ << 5) + cz;

                    if (world.isChunkLoaded(chunkX + cx, chunkZ + cz))
                        continue;

                    // local chunk coordinates need to be transformed into global ones
                    boolean didLoad = world.loadChunk(chunkX + cx, chunkZ + cz, false);
                    if(didLoad)
                        loadedCount++;

                    Chunk chunk = world.getChunkAt(chunkX + cx, chunkZ + cz);
                    if (chunk != null) {
                        loadedChunks.add(chunk);
                    } else {
                        Bukkit.getLogger().warning("Failed to load chunk at " + chunkX + ", " + chunkZ + " from " + f.getName() + ".");
                    }
                }
            }

            Bukkit.getLogger().info("Actually loaded " + loadedChunks.size() + " chunk(s) from " + f.getName() + ".");
        }

        return loadedChunks;
    }

}
