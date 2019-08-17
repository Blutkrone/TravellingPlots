package com.blutkrone.travellingplots.TravellingPlotV3.Tasks;

import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.IBuildInstruction;
import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.ITravellingPlot;
import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.IWorldOperationProgress;
import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.IWorldOperator;
import com.blutkrone.travellingplots.TravellingPlotV3.TravellingPlotHandler;
import com.blutkrone.travellingplots.TravellingPlots;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.LinkedList;

public class ResetPlotTask implements IPendingTask {

    private final TravellingPlotHandler handler;
    private final ITravellingPlot plot;
    private final Collection<IWorldOperationProgress> operationProgresses = new LinkedList<>();

    public ResetPlotTask(TravellingPlotHandler handler, ITravellingPlot plot) {
        TravellingPlots.log(0, "> Plot reset requested");
        this.handler = handler;
        this.plot = plot;

        plot.bindToOwner(null);
        TravellingPlots.log(0, "> Unbinding Plot ...");
        plot.occupyWithTask(this);
        TravellingPlots.log(0, "> Linking to task ...");

        // query every plot for cleanup
        plot.getLinkedChunks().forEach(chunk -> {
            new BukkitRunnable() {
                @Override
                public void run() {
                    File restoringFile;
                    try {
                        restoringFile = handler.getIOHandler().getRestoringFile(chunk);
                        TravellingPlots.log(1, "> Restoration file has been loaded");
                    } catch (IOException e) {
                        operationProgresses.add(null);
                        throw new RuntimeException(e);
                    }

                    try {
                        // the instructions which are contained within the file
                        AbstractQueue<? extends IBuildInstruction> iBuildInstructions = handler.getIOHandler().readInstructions(restoringFile);
                        TravellingPlots.log(1, "> Query of " + iBuildInstructions.size() + " elements has been generated");
                        // the world operator we want to operate through
                        IWorldOperator worldOperator = handler.getWorldOperator(chunk.getWorld());
                        // the queried clean-up operation.
                        IWorldOperationProgress iWorldOperationProgress = worldOperator.queueIntoApproximateChunk(iBuildInstructions, (inst) -> {
                            return inst.createCloneAt(inst.getX() + chunk.getX() * 16, inst.getY(), inst.getZ() + chunk.getZ() * 16);
                        });
                        TravellingPlots.log(1, "> Query of " + iBuildInstructions.size() + " successfully queued into world operator.");

                        // query the operation so we can track it.
                        synchronized (operationProgresses) {
                            operationProgresses.add(iWorldOperationProgress);
                        }

                        // delete the now unnecessary file
                        restoringFile.delete();
                    } catch (IOException e) {
                        Bukkit.getLogger().warning("Restoration of " + chunk.getWorld() + " " + chunk.getX() + " " + chunk.getZ()
                                + " has failed because the restoring file couldn't be read.");
                    }
                }
            }.runTaskAsynchronously(handler.getPlugin());
        });
    }

    private boolean queryingHasFinished() {
        synchronized (operationProgresses) {
            // check if all operations are queried
            if (plot.getLinkedChunks().size() != operationProgresses.size()) return false;
            // check if the queried operations are all finished
            for (IWorldOperationProgress operationProgress : operationProgresses) {
                if (operationProgress != null && operationProgress.finishedOperations() < operationProgress.totalOperations()) {
                    return false;
                }
            }
            // all operations have finished querying.
            return true;
        }
    }

    @Override
    public boolean isFinished() {
        return queryingHasFinished();
    }
}