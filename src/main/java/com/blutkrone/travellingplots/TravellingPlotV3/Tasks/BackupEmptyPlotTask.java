package com.blutkrone.travellingplots.TravellingPlotV3.Tasks;

import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.ITravellingPlot;
import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.Collection.BuildInstructionQueue;
import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.Serializable.TBuildInstruction;
import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Util.BlockStateLookup;
import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Util.TileEntityStorage;
import com.blutkrone.travellingplots.TravellingPlotV3.TravellingPlotHandler;
import com.blutkrone.travellingplots.TravellingPlots;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class BackupEmptyPlotTask implements IPendingTask {

    private int progress = 0;
    private int need;

    public BackupEmptyPlotTask(TravellingPlotHandler handler, ITravellingPlot plot) {
        if (plot.getCurrentOwner().isPresent()) throw new IllegalArgumentException("Plot cannot have an owner.");
        TravellingPlots.log(0, "> Empty plot backup requested");
        TravellingPlots.log(0, "> Linking to task ...");
        plot.occupyWithTask(this);

        need = plot.getLinkedChunks().size();
        Iterator<Chunk> iterator = plot.getLinkedChunks().iterator();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!iterator.hasNext()) {
                    cancel();
                    return;
                }

                Chunk chunk = iterator.next();
                File lastBackup;
                try {
                    lastBackup = handler.getIOHandler().getRestoringFile(chunk);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                if (lastBackup.exists()) {
                    // there is an existing backup already.
                    TravellingPlots.log(2, "> Using existing backup.");
                    synchronized (BackupEmptyPlotTask.this) {
                        progress = progress + 1;
                    }
                    return;
                }

                ChunkSnapshot chunkSnapshot = chunk.getChunkSnapshot(false, false, false);
                BlockStateLookup lookup = new TileEntityStorage(chunk, handler.getWorldOperator(chunk.getWorld()));

                TravellingPlots.log(1, "> Taking snapshot of " + chunk.getX() + " " + chunk.getZ());

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        BuildInstructionQueue instructionQueue = TBuildInstruction.convertFromSnapshot(chunkSnapshot, lookup);
                        TravellingPlots.log(1, "> Snapshot has been converted successfully.");
                        try {
                            handler.getIOHandler().saveInstructions(instructionQueue, lastBackup);
                            TravellingPlots.log(2, "> Converted instruction query has been saved.");
                            synchronized (BackupEmptyPlotTask.this) {
                                progress = progress + 1;
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }.runTaskAsynchronously(handler.getPlugin());
            }
        }.runTaskTimer(handler.getPlugin(), 0L, 1L);
    }

    @Override
    public boolean isFinished() {
        synchronized (this) {
            return progress >= need;
        }
    }

    private class ByteArrayContainer {
        public final byte[] container;

        private ByteArrayContainer(byte[] container) {
            this.container = container;
        }
    }
}
