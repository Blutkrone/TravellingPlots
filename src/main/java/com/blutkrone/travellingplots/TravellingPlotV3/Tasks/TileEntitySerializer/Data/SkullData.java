package com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Data;

import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.AbstractData;
import org.bukkit.SkullType;
import org.bukkit.block.BlockFace;

public class SkullData extends AbstractData {
    public long mostSig = -1, leastSig = -1;
    public SkullType skullType;
    public BlockFace rotation;
}
