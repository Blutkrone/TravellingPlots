package com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer;

import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.TIOHandler;
import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Data.SkullData;
import org.bukkit.Bukkit;

import java.util.UUID;

public class Skull extends AbstractSerializer<org.bukkit.block.Skull> {
    public Skull(TIOHandler ioHandler) {
        super(ioHandler);
    }

    @Override
    public Class<org.bukkit.block.Skull> getTargetClass() {
        return org.bukkit.block.Skull.class;
    }

    @Override
    public byte[] deserialize(org.bukkit.block.Skull state) {
        SkullData data = new SkullData();
        if (state.getOwningPlayer() != null) {
            UUID uuid = state.getOwningPlayer().getUniqueId();
            data.mostSig = uuid.getMostSignificantBits();
            data.leastSig = uuid.getLeastSignificantBits();
        }
        data.skullType = state.getSkullType();
        data.rotation = state.getRotation();
        return data.getAsByteArray(this);
    }

    @Override
    public void serialize(org.bukkit.block.Skull state, byte[] deserialized) throws Exception {
        SkullData data = loadData(deserialized, SkullData.class);
        state.setSkullType(data.skullType);
        state.setRotation(data.rotation);
        if (data.mostSig != -1 && data.leastSig != -1) {
            state.setOwningPlayer(Bukkit.getOfflinePlayer(new UUID(data.leastSig, data.mostSig)));
        }
    }

}
