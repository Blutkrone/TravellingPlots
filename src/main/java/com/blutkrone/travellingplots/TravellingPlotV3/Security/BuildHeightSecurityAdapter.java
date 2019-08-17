package com.blutkrone.travellingplots.TravellingPlotV3.Security;

import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.ITravellingPlot;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BuildHeightSecurityAdapter implements ISecurityAdapter {

    private final int buildHeightDown;
    private final int buildHeightUp;

    public BuildHeightSecurityAdapter(int buildHeightUp, int buildHeightDown) {
        this.buildHeightDown = buildHeightDown;
        this.buildHeightUp = buildHeightUp;
    }

    @Override
    public boolean allow(ITravellingPlot plotLocatedOn, BlockBreakEvent event) {
        int anchorHeight = plotLocatedOn.getAnchorLocation().getBlockY();
        Block wantToBreak = event.getBlock();
        Player player = event.getPlayer();

        // exceeding height limit (upwards)
        if (anchorHeight + buildHeightUp <= wantToBreak.getY()) {
            player.sendMessage(ChatColor.RED + "You cannot build at this height!");
            return false;
        }

        // exceeding height limit (downwards)
        if (anchorHeight - buildHeightDown >= wantToBreak.getY()) {
            player.sendMessage(ChatColor.RED + "You cannot build at this height!");
            return false;
        }

        return true;
    }

    @Override
    public boolean allow(ITravellingPlot plotLocatedOn, BlockPlaceEvent event) {
        int anchorHeight = plotLocatedOn.getAnchorLocation().getBlockY();
        Block wantToPlaceOn = event.getBlock();
        Player player = event.getPlayer();

        // exceeding height limit (upwards)
        if (anchorHeight + buildHeightUp <= wantToPlaceOn.getY()) {
            player.sendMessage(ChatColor.RED + "You cannot build at this height!");
            return false;
        }

        // exceeding height limit (downwards)
        if (anchorHeight - buildHeightDown >= wantToPlaceOn.getY()) {
            player.sendMessage(ChatColor.RED + "You cannot build at this height!");
            return false;
        }

        return true;
    }
}
