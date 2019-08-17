package com.blutkrone.travellingplots.TravellingPlotV3.Security;

import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.TTravellingPlot;
import com.blutkrone.travellingplots.TravellingPlots;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.List;

public class SecurityAdapterListener implements Listener {

    private final TravellingPlots plugin;
    private List<ISecurityAdapter> securityAdapters = new ArrayList<>();

    public SecurityAdapterListener(TravellingPlots plugin) {
        this.plugin = plugin;
    }

    public List<ISecurityAdapter> getSecurityAdapters() {
        return securityAdapters;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    void on(PlayerTeleportEvent event) {
        if (event.getTo() != null) {
            TTravellingPlot registeredPlotBy = plugin.getTravellingPlotHandler().getRegisteredPlotBy(event.getTo().getChunk());
            if (registeredPlotBy == null) return;

            for (ISecurityAdapter securityAdapter : securityAdapters) {
                if (!securityAdapter.allow(registeredPlotBy, event)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    void on(EntityTeleportEvent event) {
        if (event.getTo() != null) {
            TTravellingPlot registeredPlotBy = plugin.getTravellingPlotHandler().getRegisteredPlotBy(event.getTo().getChunk());
            if (registeredPlotBy == null) return;

            for (ISecurityAdapter securityAdapter : securityAdapters) {
                if (!securityAdapter.allow(registeredPlotBy, event)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    void on(EntitySpawnEvent event) {
        TTravellingPlot registeredPlotBy = plugin.getTravellingPlotHandler().getRegisteredPlotBy(event.getLocation().getChunk());
        if (registeredPlotBy == null) return;

        for (ISecurityAdapter securityAdapter : securityAdapters) {
            if (!securityAdapter.allow(registeredPlotBy, event)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    void on(EntityInteractEvent event) {
        TTravellingPlot registeredPlotBy = plugin.getTravellingPlotHandler().getRegisteredPlotBy(event.getBlock().getLocation().getChunk());
        if (registeredPlotBy == null) return;

        for (ISecurityAdapter securityAdapter : securityAdapters) {
            if (!securityAdapter.allow(registeredPlotBy, event)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    void on(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        TTravellingPlot registeredPlotBy = plugin.getTravellingPlotHandler().getRegisteredPlotBy(event.getClickedBlock().getLocation().getChunk());
        if (registeredPlotBy == null) return;

        for (ISecurityAdapter securityAdapter : securityAdapters) {
            if (!securityAdapter.allow(registeredPlotBy, event)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    void on(PlayerInteractEntityEvent event) {
        TTravellingPlot registeredPlotBy = plugin.getTravellingPlotHandler().getRegisteredPlotBy(event.getRightClicked().getLocation().getChunk());
        if (registeredPlotBy == null) return;

        for (ISecurityAdapter securityAdapter : securityAdapters) {
            if (!securityAdapter.allow(registeredPlotBy, event)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    void on(PlayerInteractAtEntityEvent event) {
        TTravellingPlot registeredPlotBy = plugin.getTravellingPlotHandler().getRegisteredPlotBy(event.getRightClicked().getLocation().getChunk());
        if (registeredPlotBy == null) return;

        for (ISecurityAdapter securityAdapter : securityAdapters) {
            if (!securityAdapter.allow(registeredPlotBy, event)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    void on(BlockBreakEvent event) {
        TTravellingPlot registeredPlotBy = plugin.getTravellingPlotHandler().getRegisteredPlotBy(event.getBlock().getLocation().getChunk());
        if (registeredPlotBy == null) {
            if (event.getPlayer().hasPermission("tplot.freebuild")) return;
            event.setCancelled(true);
            return;
        }

        for (ISecurityAdapter securityAdapter : securityAdapters) {
            if (!securityAdapter.allow(registeredPlotBy, event)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    void on(BlockPlaceEvent event) {
        TTravellingPlot registeredPlotBy = plugin.getTravellingPlotHandler().getRegisteredPlotBy(event.getBlock().getLocation().getChunk());
        if (registeredPlotBy == null) {
            if (event.getPlayer().hasPermission("tplot.freebuild")) return;
            event.setCancelled(true);
            return;
        }

        for (ISecurityAdapter securityAdapter : securityAdapters) {
            if (!securityAdapter.allow(registeredPlotBy, event)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    void on(EntityDamageByBlockEvent event) {
        TTravellingPlot registeredPlotBy = plugin.getTravellingPlotHandler().getRegisteredPlotBy(event.getEntity().getLocation().getChunk());
        if (registeredPlotBy == null) return;

        for (ISecurityAdapter securityAdapter : securityAdapters) {
            if (!securityAdapter.allow(registeredPlotBy, event)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    void on(EntityDamageByEntityEvent event) {
        TTravellingPlot registeredPlotBy = plugin.getTravellingPlotHandler().getRegisteredPlotBy(event.getEntity().getLocation().getChunk());
        if (registeredPlotBy == null) return;

        for (ISecurityAdapter securityAdapter : securityAdapters) {
            if (!securityAdapter.allow(registeredPlotBy, event)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    void on(EntityDamageEvent event) {
        TTravellingPlot registeredPlotBy = plugin.getTravellingPlotHandler().getRegisteredPlotBy(event.getEntity().getLocation().getChunk());
        if (registeredPlotBy == null) return;

        for (ISecurityAdapter securityAdapter : securityAdapters) {
            if (!securityAdapter.allow(registeredPlotBy, event)) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
