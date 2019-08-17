package com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Data;

import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.AbstractData;
import org.bukkit.entity.EntityType;

public class CreatureSpawnerData extends AbstractData {
    public int delay, maxnearby, maxdelay, mindelay, playerrange, spawnrange;
    public EntityType type;
}
