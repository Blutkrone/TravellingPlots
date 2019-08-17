package com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Util;

import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.IWorldOperator;
import org.bukkit.Chunk;
import org.bukkit.block.BlockState;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class TileEntityStorage implements BlockStateLookup {

    private Map<Vector, ByteArrayContainer> wrappedContainers = new HashMap<>();

    public TileEntityStorage(Chunk chunk, IWorldOperator worldOperator) {
        for (BlockState blockState : chunk.getTileEntities()) {
            int x = blockState.getX() & 0x000F;
            int y = blockState.getY() & 0x00FF;
            int z = blockState.getZ() & 0x000F;
            wrappedContainers.put(new Vector(x, y, z),
                    new ByteArrayContainer(worldOperator.saveBlockState(blockState)));
        }
    }

    @Override
    public byte[] getBlockStateFor(int x, int y, int z) {
        ByteArrayContainer byteArrayContainer = wrappedContainers.get(new Vector(x, y, z));
        if (byteArrayContainer == null) return null;
        return byteArrayContainer.container;
    }
}
