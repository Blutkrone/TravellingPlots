package com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer;

import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.TIOHandler;
import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Data.BrewingStandData;

public class BrewingStand extends AbstractSerializer<org.bukkit.block.BrewingStand> {
    public BrewingStand(TIOHandler ioHandler) {
        super(ioHandler);
    }

    @Override
    public Class<org.bukkit.block.BrewingStand> getTargetClass() {
        return org.bukkit.block.BrewingStand.class;
    }

    @Override
    public byte[] deserialize(org.bukkit.block.BrewingStand blockState) {
        BrewingStandData data = new BrewingStandData();
        data.saveInventory(blockState);
        return data.getAsByteArray(this);
    }

    @Override
    public void serialize(org.bukkit.block.BrewingStand state, byte[] deserialized) throws Exception {
        BrewingStandData data = loadData(deserialized, BrewingStandData.class);
        data.loadInventory(state);
    }
}
