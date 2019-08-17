package com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer;

import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.TIOHandler;
import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Data.FlowerPotData;
import org.bukkit.material.MonsterEggs;

public class FlowerPot extends AbstractSerializer<org.bukkit.block.FlowerPot> {
    public FlowerPot(TIOHandler ioHandler) {
        super(ioHandler);
    }

    @Override
    public Class<org.bukkit.block.FlowerPot> getTargetClass() {
        return org.bukkit.block.FlowerPot.class;
    }

    @Override
    public byte[] deserialize(org.bukkit.block.FlowerPot state) {
        FlowerPotData data = new FlowerPotData();
        if (state.getContents() != null) {
            data.data = state.getContents().getData();
            data.material = state.getContents().getItemType();
        }
        return data.getAsByteArray(this);
    }

    @Override
    public void serialize(org.bukkit.block.FlowerPot state, byte[] deserialized) throws Exception {
        FlowerPotData data = loadData(deserialized, FlowerPotData.class);
        if (data.material != null) {
            state.setContents(new MonsterEggs(data.material, data.data));
        }
    }

}
