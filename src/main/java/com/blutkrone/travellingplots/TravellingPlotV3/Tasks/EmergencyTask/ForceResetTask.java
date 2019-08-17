package com.blutkrone.travellingplots.TravellingPlotV3.Tasks.EmergencyTask;

import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.IBuildInstruction;
import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.ITravellingPlot;
import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.IWorldOperator;
import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.IPendingTask;
import com.blutkrone.travellingplots.TravellingPlotV3.TravellingPlotHandler;
import com.blutkrone.travellingplots.TravellingPlots;
import com.blutkrone.travellingplots.Util.ChunkLoadHandler;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;

import java.io.File;
import java.io.IOException;
import java.util.AbstractQueue;

public class ForceResetTask implements IPendingTask, IEmergencyTask {

    public ForceResetTask(TravellingPlotHandler handler, ITravellingPlot plot) {
        TravellingPlots.log(0, "> Force reset requested");

        plot.bindToOwner(null);
        TravellingPlots.log(0, "> Unbinding Plot ...");
        plot.occupyWithTask(this);
        TravellingPlots.log(0, "> Linking to task ...");

        for (Chunk chunk : plot.getLinkedChunks()) {
            ChunkLoadHandler loadHandler = new ChunkLoadHandler(chunk);

            File restoringFile;
            try {
                restoringFile = handler.getIOHandler().getRestoringFile(chunk);
                TravellingPlots.log(1, "> Restoration file has been loaded");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                // the instructions which are contained within the file
                AbstractQueue<? extends IBuildInstruction> iBuildInstructions = handler.getIOHandler().readInstructions(restoringFile);
                TravellingPlots.log(1, "> Query of " + iBuildInstructions.size() + " elements has been generated");
                // the world operator we want to operate through
                IWorldOperator worldOperator = handler.getWorldOperator(chunk.getWorld());
                // the queried clean-up operation.
                worldOperator.queueIntoApproximateChunk(iBuildInstructions, (inst) -> {
                    return inst.createCloneAt(inst.getX() + chunk.getX() * 16, inst.getY(), inst.getZ() + chunk.getZ() * 16);
                });
                TravellingPlots.log(1, "> Query of " + iBuildInstructions.size() + " successfully queued into world operator.");

                // delete the now unnecessary file
                restoringFile.delete();
            } catch (IOException e) {
                Bukkit.getLogger().warning("Restoration of " + chunk.getWorld() + " " + chunk.getX() + " " + chunk.getZ()
                        + " has failed because the restoring file couldn't be read.");
            }

            loadHandler.unload();
        }
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
