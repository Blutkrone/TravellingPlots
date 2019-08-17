package com.blutkrone.travellingplots.TravellingPlotV3.Security;

import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.ITravellingPlot;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntitySpawnEvent;

public class EntityNoSpawnSecurityAdapter implements ISecurityAdapter {
    @Override
    public boolean allow(ITravellingPlot plotLocatedOn, EntitySpawnEvent wantToSpawn) {
        return !(wantToSpawn.getEntity() instanceof LivingEntity);
    }
}