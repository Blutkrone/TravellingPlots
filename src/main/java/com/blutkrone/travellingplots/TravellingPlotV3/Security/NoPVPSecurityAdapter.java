package com.blutkrone.travellingplots.TravellingPlotV3.Security;

import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.ITravellingPlot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class NoPVPSecurityAdapter implements ISecurityAdapter {
    @Override
    public boolean allow(ITravellingPlot plotLocatedOn, EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getDamager() instanceof Player) {
                return false;
            } else if (event.getDamager() instanceof Projectile) {
                return !(((Projectile) event.getDamager()).getShooter() instanceof Player);
            }
        }

        return true;
    }
}
