package com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer;

import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.TIOHandler;
import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Data.NoteBlockData;
import org.bukkit.Note;

public class NoteBlock extends AbstractSerializer<org.bukkit.block.NoteBlock> {
    public NoteBlock(TIOHandler ioHandler) {
        super(ioHandler);
    }

    @Override
    public Class<org.bukkit.block.NoteBlock> getTargetClass() {
        return org.bukkit.block.NoteBlock.class;
    }

    @Override
    public byte[] deserialize(org.bukkit.block.NoteBlock state) {
        NoteBlockData data = new NoteBlockData();
        data.oct = state.getNote().getOctave();
        data.tone = state.getNote().getTone();
        data.sharp = state.getNote().isSharped();
        return data.getAsByteArray(this);
    }

    @Override
    public void serialize(org.bukkit.block.NoteBlock state, byte[] deserialized) throws Exception {
        NoteBlockData data = loadData(deserialized, NoteBlockData.class);
        state.setNote(new Note(data.oct, data.tone, data.sharp));
    }

}
