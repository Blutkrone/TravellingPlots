package com.blutkrone.travellingplots.TravellingPlotV3.Tasks.EmergencyTask;

import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.ITravellingPlot;
import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.Collection.BuildInstructionQueue;
import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.Serializable.TBuildInstruction;
import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.IPendingTask;
import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Util.BlockStateLookup;
import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Util.TileEntityStorage;
import com.blutkrone.travellingplots.TravellingPlotV3.TravellingPlotHandler;
import com.blutkrone.travellingplots.TravellingPlots;
import com.blutkrone.travellingplots.Util.ChunkLoadHandler;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;

import java.io.IOException;

public class ForceSaveTask implements IPendingTask {

    /**
     * Bulk operation which will block the main thread!
     */
    public ForceSaveTask(TravellingPlotHandler handler, ITravellingPlot plot) {
        TravellingPlots.log(0, "> Force saving requested");
        if (!plot.getCurrentOwner().isPresent()) throw new IllegalArgumentException("Plot has no owner.");
        plot.occupyWithTask(this);
        OfflinePlayer oldOwner = plot.getCurrentOwner().get();

        try {
            TravellingPlots.log(0, "> Signature saving ...");
            handler.getIOHandler().saveSignature(oldOwner, plot);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Chunk chunk : plot.getLinkedChunks()) {
            ChunkLoadHandler loader = new ChunkLoadHandler(chunk);
            BlockStateLookup lookup = new TileEntityStorage(chunk, handler.getWorldOperator(chunk.getWorld()));

            int x = plot.getRelativeX(chunk), z = plot.getRelativeZ(chunk);
            BuildInstructionQueue instructionQueue = TBuildInstruction.convertFromChunk(chunk, lookup);
            TravellingPlots.log(1, "> Chunk has been converted successfully.");
            try {
                handler.getIOHandler().saveInstructions(oldOwner, x, z, instructionQueue);
                TravellingPlots.log(2, "> Converted instruction query has been saved.");
            } catch (IOException e) {
                e.printStackTrace();
            }

            loader.unload();
        }
    }

    @Override
    public boolean isFinished() {
        return true;
    }

    private class ByteArrayContainer {
        public final byte[] container;

        private ByteArrayContainer(byte[] container) {
            this.container = container;
        }
    }
}
