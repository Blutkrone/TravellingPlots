package com.blutkrone.travellingplots.TravellingPlotV3.Security;

import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.ITravellingPlot;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlockedDamageCauses implements ISecurityAdapter {

    private Map<String, Set<String>> blockedByPermission = new HashMap<>();

    public BlockedDamageCauses(ConfigurationSection configurationSection) {
        if (configurationSection == null) return;
        for (String s : configurationSection.getKeys(false)) {
            blockedByPermission.put(s.replace(" ", "."), new HashSet<>(configurationSection.getStringList(s)));
        }
    }

    @Override
    public boolean allow(ITravellingPlot plotLocatedOn, EntityDamageEvent event) {
        for (Map.Entry<String, Set<String>> stringSetEntry : blockedByPermission.entrySet()) {
            if (event.getEntity().hasPermission(stringSetEntry.getKey())) {
                if (stringSetEntry.getValue().contains(event.getCause().name())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean allow(ITravellingPlot plotLocatedOn, EntityDamageByEntityEvent event) {
        for (Map.Entry<String, Set<String>> stringSetEntry : blockedByPermission.entrySet()) {
            if (event.getEntity().hasPermission(stringSetEntry.getKey())) {
                if (stringSetEntry.getValue().contains(event.getCause().name())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean allow(ITravellingPlot plotLocatedOn, EntityDamageByBlockEvent event) {
        for (Map.Entry<String, Set<String>> stringSetEntry : blockedByPermission.entrySet()) {
            if (event.getEntity().hasPermission(stringSetEntry.getKey())) {
                if (stringSetEntry.getValue().contains(event.getCause().name())) {
                    return false;
                }
            }
        }
        return true;
    }
}
