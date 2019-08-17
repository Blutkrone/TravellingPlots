package com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer;

import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.TIOHandler;
import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Data.ChestData;

public class Chest extends AbstractSerializer<org.bukkit.block.Chest> {
    public Chest(TIOHandler ioHandler) {
        super(ioHandler);
    }

    @Override
    public Class<org.bukkit.block.Chest> getTargetClass() {
        return org.bukkit.block.Chest.class;
    }

    @Override
    public byte[] deserialize(org.bukkit.block.Chest state) {
        ChestData data = new ChestData();
        if (state.isLocked()) data.lock = state.getLock();
        data.name = state.getCustomName();
        data.saveInventory(state);
        return data.getAsByteArray(this);
    }

    @Override
    public void serialize(org.bukkit.block.Chest state, byte[] deserialized) throws Exception {
        ChestData data = loadData(deserialized, ChestData.class);
        if (data.name != null) state.setCustomName(data.name);
        if (data.lock != null) state.setLock(data.lock);
        data.loadInventory(state);
    }
}
