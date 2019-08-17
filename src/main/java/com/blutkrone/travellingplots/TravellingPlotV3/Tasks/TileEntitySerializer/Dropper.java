package com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer;

import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.TIOHandler;
import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Data.DropperData;

public class Dropper extends AbstractSerializer<org.bukkit.block.Dropper> {
    public Dropper(TIOHandler ioHandler) {
        super(ioHandler);
    }

    @Override
    public Class<org.bukkit.block.Dropper> getTargetClass() {
        return org.bukkit.block.Dropper.class;
    }

    @Override
    public byte[] deserialize(org.bukkit.block.Dropper state) {
        DropperData data = new DropperData();
        data.saveInventory(state);
        return data.getAsByteArray(this);
    }

    @Override
    public void serialize(org.bukkit.block.Dropper state, byte[] deserialized) throws Exception {
        DropperData data = loadData(deserialized, DropperData.class);
        data.loadInventory(state);
    }
}
