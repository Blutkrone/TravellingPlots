package com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer;

import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.TIOHandler;
import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Data.ShulkerBoxData;

public class ShulkerBox extends AbstractSerializer<org.bukkit.block.ShulkerBox> {
    public ShulkerBox(TIOHandler ioHandler) {
        super(ioHandler);
    }

    @Override
    public Class<org.bukkit.block.ShulkerBox> getTargetClass() {
        return org.bukkit.block.ShulkerBox.class;
    }

    @Override
    public byte[] deserialize(org.bukkit.block.ShulkerBox state) {
        ShulkerBoxData data = new ShulkerBoxData();
        data.saveInventory(state);
        return data.getAsByteArray(this);
    }

    @Override
    public void serialize(org.bukkit.block.ShulkerBox state, byte[] deserialized) throws Exception {
        ShulkerBoxData data = loadData(deserialized, ShulkerBoxData.class);
        data.loadInventory(state);
    }

}
