package com.blutkrone.travellingplots.Util;

import org.bukkit.Chunk;

public class ChunkLoadHandler {

    private final boolean loaded;
    private final Chunk chunk;

    /**
     * A simple chunk loader implementation, handles loading/unloading
     */
    public ChunkLoadHandler(Chunk chunk) {
        loaded = chunk.isLoaded();
        if (!loaded) chunk.load(false);
        this.chunk = chunk;
    }

    public void unload() {
        if (loaded) chunk.unload(false);
    }
}
