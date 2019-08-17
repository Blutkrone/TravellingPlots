package com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer;

import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.TIOHandler;
import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Data.DispenserData;

public class Dispenser extends AbstractSerializer<org.bukkit.block.Dispenser> {
    public Dispenser(TIOHandler ioHandler) {
        super(ioHandler);
    }

    @Override
    public Class<org.bukkit.block.Dispenser> getTargetClass() {
        return org.bukkit.block.Dispenser.class;
    }

    @Override
    public byte[] deserialize(org.bukkit.block.Dispenser state) {
        DispenserData dispenserData = new DispenserData();
        dispenserData.saveInventory(state);
        return dispenserData.getAsByteArray(this);
    }

    @Override
    public void serialize(org.bukkit.block.Dispenser state, byte[] deserialized) throws Exception {
        DispenserData dispenserData = loadData(deserialized, DispenserData.class);
        dispenserData.loadInventory(state);
    }

}
