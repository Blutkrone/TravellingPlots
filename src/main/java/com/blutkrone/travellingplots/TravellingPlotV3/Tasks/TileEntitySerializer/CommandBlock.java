package com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer;

import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.TIOHandler;
import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Data.CommandBlockData;

public class CommandBlock extends AbstractSerializer<org.bukkit.block.CommandBlock> {
    public CommandBlock(TIOHandler ioHandler) {
        super(ioHandler);
    }

    @Override
    public Class<org.bukkit.block.CommandBlock> getTargetClass() {
        return org.bukkit.block.CommandBlock.class;
    }

    @Override
    public byte[] deserialize(org.bukkit.block.CommandBlock state) {
        CommandBlockData data = new CommandBlockData();
        data.name = state.getName();
        data.cmd = state.getCommand();
        return data.getAsByteArray(this);
    }

    @Override
    public void serialize(org.bukkit.block.CommandBlock state, byte[] deserialized) throws Exception {
        CommandBlockData data = loadData(deserialized, CommandBlockData.class);
        if (data.name != null) state.setName(data.name);
        if (data.cmd != null) state.setCommand(data.cmd);
    }

}
