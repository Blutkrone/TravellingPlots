package com.blutkrone.travellingplots.TravellingPlotV3.Security;

import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.ITravellingPlot;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public interface ISecurityAdapter {

    default boolean allow(ITravellingPlot plotLocatedOn, PlayerTeleportEvent teleportEvent) {
        return true;
    }

    default boolean allow(ITravellingPlot plotLocatedOn, EntityTeleportEvent teleportEvent) {
        return true;
    }

    default boolean allow(ITravellingPlot plotLocatedOn, EntitySpawnEvent wantToSpawn) {
        return true;
    }

    default boolean allow(ITravellingPlot plotLocatedOn, EntityInteractEvent player) {
        return true;
    }

    default boolean allow(ITravellingPlot plotLocatedOn, PlayerInteractEvent player) {
        return true;
    }

    default boolean allow(ITravellingPlot plotLocatedOn, PlayerInteractAtEntityEvent player) {
        return true;
    }

    default boolean allow(ITravellingPlot plotLocatedOn, PlayerInteractEntityEvent player) {
        return true;
    }

    default boolean allow(ITravellingPlot plotLocatedOn, BlockBreakEvent event) {
        return true;
    }

    default boolean allow(ITravellingPlot plotLocatedOn, BlockPlaceEvent event) {
        return true;
    }

    default boolean allow(ITravellingPlot plotLocatedOn, EntityDamageByEntityEvent event) {
        return true;
    }

    default boolean allow(ITravellingPlot plotLocatedOn, EntityDamageByBlockEvent event) {
        return true;
    }

    default boolean allow(ITravellingPlot plotLocatedOn, EntityDamageEvent event) {
        return true;
    }
}
