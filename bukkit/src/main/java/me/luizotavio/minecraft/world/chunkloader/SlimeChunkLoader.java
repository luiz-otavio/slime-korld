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

package me.luizotavio.minecraft.world.chunkloader;

import gnu.trove.map.hash.TLongObjectHashMap;
import me.luizotavio.minecraft.prototype.chunk.ProtoSlimeChunk;
import me.luizotavio.minecraft.world.CraftSlimeWorld;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.util.LongHash;

import java.io.IOException;

/**
 * @author Luiz Otávio de Farias Corrêa
 * @since 13/08/2022
 */
public class SlimeChunkLoader implements IChunkLoader {

    private final TLongObjectHashMap<ProtoSlimeChunk> chunks;

    public SlimeChunkLoader(TLongObjectHashMap<ProtoSlimeChunk> chunks) {
        this.chunks = chunks;
    }

    @Override
    public Chunk a(World world, int i, int i1) throws IOException {
        return ((WorldServer) world).chunkProviderServer
            .emptyChunk;
    }

    /**
     * Save method to save each chunk to the file system.
     */
    @Override
    public void a(World world, Chunk chunk) throws IOException, ExceptionWorldConflict {
    }

    /**
     * Save method to save each chunk to the file system.
     */
    @Override
    public void b(World world, Chunk chunk) throws IOException {
    }

    @Override
    public void a() {
    }

    @Override
    public void b() {
    }

    /**
     * Loads all chunks from the file system.
     *
     * @param craftSlimeWorld The world to load the chunk into.
     */
    public void loadAll(CraftSlimeWorld craftSlimeWorld) {
        ChunkProviderServer chunkProviderServer = craftSlimeWorld.chunkProviderServer;

        for (ProtoSlimeChunk chunk : chunks.valueCollection()) {
            Chunk target = chunk.toChunk(craftSlimeWorld);

            chunkProviderServer.chunks.put(
                LongHash.toLong(target.locX, target.locZ),
                target
            );

            target.addEntities();
        }
    }
}
