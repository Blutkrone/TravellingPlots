package com.blutkrone.travellingplots.TravellingPlotV3.Security;

import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.ITravellingPlot;
import org.bukkit.ChatColor;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class DisallowModifyingEmptyPlotSecurityAdapter implements ISecurityAdapter {
    @Override
    public boolean allow(ITravellingPlot plotLocatedOn, BlockBreakEvent event) {
        if (plotLocatedOn.getCurrentOwner().isPresent()) return true;
        event.getPlayer().sendMessage(ChatColor.RED + "You must own the plot to do this (Right-Click the Corner Stone!");
        return false;
    }

    @Override
    public boolean allow(ITravellingPlot plotLocatedOn, BlockPlaceEvent event) {
        if (plotLocatedOn.getCurrentOwner().isPresent()) return true;
        event.getPlayer().sendMessage(ChatColor.RED + "You must own the plot to do this (Right-Click the Corner Stone!");
        return false;
    }
}
