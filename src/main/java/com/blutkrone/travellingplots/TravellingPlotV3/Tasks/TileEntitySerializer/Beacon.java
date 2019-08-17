package com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer;

import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.TIOHandler;
import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Data.BeaconData;
import org.bukkit.potion.PotionEffectType;

public class Beacon extends AbstractSerializer<org.bukkit.block.Beacon> {
    public Beacon(TIOHandler ioHandler) {
        super(ioHandler);
    }

    @Override
    public Class<org.bukkit.block.Beacon> getTargetClass() {
        return org.bukkit.block.Beacon.class;
    }

    @Override
    public byte[] deserialize(org.bukkit.block.Beacon state) {
        state.getInventory().getItem();
        BeaconData data = new BeaconData();
        data.saveInventory(state);
        if (state.getPrimaryEffect() != null) {
            data.feff = state.getPrimaryEffect().getType().getName();
        }
        if (state.getSecondaryEffect() != null) {
            data.seff = state.getSecondaryEffect().getType().getName();
        }
        ;
        return data.getAsByteArray(this);
    }

    @Override
    public void serialize(org.bukkit.block.Beacon state, byte[] deserialized) throws Exception {
        BeaconData data = loadData(deserialized, BeaconData.class);
        data.loadInventory(state);
        if (data.feff != null) state.setPrimaryEffect(PotionEffectType.getByName(data.feff));
        if (data.seff != null) state.setPrimaryEffect(PotionEffectType.getByName(data.seff));
    }
}
