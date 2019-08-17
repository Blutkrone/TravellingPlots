package com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer;

import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.TIOHandler;
import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Data.SignData;

public class Sign extends AbstractSerializer<org.bukkit.block.Sign> {
    public Sign(TIOHandler ioHandler) {
        super(ioHandler);
    }

    @Override
    public Class<org.bukkit.block.Sign> getTargetClass() {
        return org.bukkit.block.Sign.class;
    }

    @Override
    public byte[] deserialize(org.bukkit.block.Sign state) {
        SignData data = new SignData();
        data.lines = state.getLines();
        return data.getAsByteArray(this);
    }

    @Override
    public void serialize(org.bukkit.block.Sign state, byte[] deserialized) throws Exception {
        SignData data = loadData(deserialized, SignData.class);
        String[] lines = data.lines;
        for (int i = 0, linesLength = lines.length; i < linesLength; i++) {
            String line = lines[i];
            state.setLine(i, line);
        }
    }

}
