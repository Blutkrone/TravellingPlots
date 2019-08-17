package com.blutkrone.travellingplots.TravellingPlotV3.Tasks;

import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.IBuildInstruction;
import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.IPlotSignature;
import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.ITravellingPlot;
import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.IWorldOperationProgress;
import com.blutkrone.travellingplots.TravellingPlotV3.TravellingPlotHandler;
import com.blutkrone.travellingplots.TravellingPlots;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.*;

public class LoadPlotTask implements IPendingTask {

    private final ITravellingPlot plot;
    private final List<IWorldOperationProgress> operationProgresses = new ArrayList<>();
    private final OfflinePlayer message;
    private IPlotSignature iPlotSignature;

    public LoadPlotTask(TravellingPlotHandler handler, ITravellingPlot plot, OfflinePlayer newOwner) {
        TravellingPlots.log(0, "> Plot load requested");
        this.plot = plot;

        plot.bindToOwner(newOwner);
        TravellingPlots.log(0, "> Rebinding Plot ...");
        plot.occupyWithTask(this);
        TravellingPlots.log(0, "> Linking to task ...");

        this.message = newOwner;

        Iterator<Chunk> iterator = plot.getLinkedChunks().iterator();
        try {
            Optional<IPlotSignature> iPlotSignature = handler.getIOHandler().readSignature(plot.getCurrentOwner().orElse(null));
            this.iPlotSignature = iPlotSignature.orElse(null);
            TravellingPlots.log(0, "> Plot signature has been loaded successfully");
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
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        AbstractQueue<? extends IBuildInstruction> instructionQueue = null;
                        try {
                            instructionQueue = handler.getIOHandler().readPlotInstructions(newOwner, plot.getRelativeX(chunk), plot.getRelativeZ(chunk));
                            TravellingPlots.log(1, "> Instructions have been loaded successfully.");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        IWorldOperationProgress iWorldOperationProgress = handler.getWorldOperator(plot.getAnchorLocation().getWorld()).queueIntoApproximateChunk(
                                instructionQueue, (instruction) -> {
                                    int yOffset = plot.getAnchorLocation().getBlockY() - (iPlotSignature == null ? 0 : iPlotSignature.yOffset());
                                    return instruction.createCloneAt(
                                            instruction.getX() + chunk.getX() * 16,
                                            instruction.getY() + (yOffset),
                                            instruction.getZ() + chunk.getZ() * 16
                                    );
                                }
                        );

                        synchronized (operationProgresses) {
                            operationProgresses.add(iWorldOperationProgress);
                        }
                    }
                }.runTaskAsynchronously(handler.getPlugin());
            }
        }.runTaskTimer(handler.getPlugin(), 1L, 1L);
    }

    @Override
    public void applyCallback() {
        final Player player = message.getPlayer();
        if (player == null) return;
        player.sendMessage(ChatColor.GREEN + "Your plot has been loaded!");
    }

    @Override
    public boolean isFinished() {
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
}
