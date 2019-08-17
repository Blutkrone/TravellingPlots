package com.blutkrone.travellingplots.TravellingPlotV3.Tasks;

import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.ITravellingPlot;
import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.Collection.BuildInstructionQueue;
import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.Serializable.TBuildInstruction;
import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.EmergencyTask.IEmergencyTask;
import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Util.BlockStateLookup;
import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Util.TileEntityStorage;
import com.blutkrone.travellingplots.TravellingPlotV3.TravellingPlotHandler;
import com.blutkrone.travellingplots.TravellingPlots;
import com.blutkrone.travellingplots.Util.ChunkLoadHandler;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.Iterator;

public class SavePlotTask implements IPendingTask, IEmergencyTask {

    private final OfflinePlayer oldOwner;
    private int blockProgress = 0;
    private int need;

    public SavePlotTask(TravellingPlotHandler handler, ITravellingPlot plot) {
        TravellingPlots.log(0, "> Plot Saving requested");
        if (!plot.getCurrentOwner().isPresent()) throw new IllegalArgumentException("Plot has no owner.");
        need = plot.getLinkedChunks().size();
        Iterator<Chunk> iterator = plot.getLinkedChunks().iterator();
        oldOwner = plot.getCurrentOwner().get();

        TravellingPlots.log(0, "> Linking to task ...");
        plot.occupyWithTask(this);

        try {
            TravellingPlots.log(0, "> Signature saving ...");
            handler.getIOHandler().saveSignature(oldOwner, plot);
        } catch (IOException e) {
            e.printStackTrace();
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!iterator.hasNext()) {
                    cancel();
                    return;
                }

                Chunk chunk = iterator.next();
                ChunkLoadHandler loader = new ChunkLoadHandler(chunk);
                ChunkSnapshot chunkSnapshot = chunk.getChunkSnapshot(false, false, false);
                int x = plot.getRelativeX(chunk), z = plot.getRelativeZ(chunk);
                TravellingPlots.log(1, "> Taking snapshot of " + chunk.getX() + " " + chunk.getZ());

                // run the blockstate serialization on the main thread
                // todo: can we run blockstate serialization off thread?
                BlockStateLookup lookup = new TileEntityStorage(chunk, handler.getWorldOperator(chunk.getWorld()));

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        BuildInstructionQueue instructionQueue = TBuildInstruction.convertFromSnapshot(chunkSnapshot, lookup);
                        TravellingPlots.log(1, "> Snapshot has been converted successfully.");
                        try {
                            handler.getIOHandler().saveInstructions(oldOwner, x, z, instructionQueue);
                            TravellingPlots.log(2, "> Converted instruction query has been saved.");

                            synchronized (SavePlotTask.this) {
                                blockProgress = blockProgress + 1;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.runTaskAsynchronously(handler.getPlugin());

                loader.unload();
            }
        }.runTaskTimer(handler.getPlugin(), 0L, 1L);
    }

    @Override
    public void applyCallback() {
        final Player player = oldOwner.getPlayer();
        if (player == null) return;
        player.sendMessage(ChatColor.GREEN + "Your plot has been saved!");
    }

    @Override
    public boolean isFinished() {
        synchronized (this) {
            return blockProgress >= need;
        }
    }

    private class ByteArrayContainer {
        public final byte[] container;

        private ByteArrayContainer(byte[] container) {
            this.container = container;
        }
    }
}
