package com.blutkrone.travellingplots.TravellingPlotV3.Tasks;

import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.ITravellingPlot;
import com.blutkrone.travellingplots.TravellingPlotV3.TravellingPlotHandler;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class RescueEntitiesTask implements IPendingTask {

    public RescueEntitiesTask(TravellingPlotHandler handler, ITravellingPlot plot) {
        for (Chunk chunk : plot.getLinkedChunks()) {
            for (Entity entity : chunk.getEntities()) {
                if (entity instanceof LivingEntity) {
                    entity.teleport(plot.getAnchorLocation().clone().add(0, 1, 0));
                }
            }
        }
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
