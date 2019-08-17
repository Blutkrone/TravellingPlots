package com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer;

import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.TIOHandler;
import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Data.FurnaceData;

public class Furnace extends AbstractSerializer<org.bukkit.block.Furnace> {
    public Furnace(TIOHandler ioHandler) {
        super(ioHandler);
    }

    @Override
    public Class<org.bukkit.block.Furnace> getTargetClass() {
        return org.bukkit.block.Furnace.class;
    }

    @Override
    public byte[] deserialize(org.bukkit.block.Furnace state) {
        FurnaceData data = new FurnaceData();
        data.saveInventory(state);
        return data.getAsByteArray(this);
    }

    @Override
    public void serialize(org.bukkit.block.Furnace state, byte[] deserialized) throws Exception {
        FurnaceData data = loadData(deserialized, FurnaceData.class);
        data.loadInventory(state);
    }

}
