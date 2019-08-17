package com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer;

import com.blutkrone.travellingplots.ItemStackSerializer;
import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.TIOHandler;
import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Data.JukeboxData;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Jukebox extends AbstractSerializer<org.bukkit.block.Jukebox> {
    public Jukebox(TIOHandler ioHandler) {
        super(ioHandler);
    }

    @Override
    public Class<org.bukkit.block.Jukebox> getTargetClass() {
        return org.bukkit.block.Jukebox.class;
    }

    @Override
    public byte[] deserialize(org.bukkit.block.Jukebox state) {
        JukeboxData data = new JukeboxData();
        data.item = ItemStackSerializer.toByteArray(state.getRecord());
        data.record = state.getPlaying();
        return data.getAsByteArray(this);
    }

    @Override
    public void serialize(org.bukkit.block.Jukebox state, byte[] deserialized) throws Exception {
        JukeboxData data = loadData(deserialized, JukeboxData.class);
        state.setPlaying(data.record);
        state.setRecord(data.getItemOrElse(data.item, new ItemStack(Material.AIR)));
    }

}
