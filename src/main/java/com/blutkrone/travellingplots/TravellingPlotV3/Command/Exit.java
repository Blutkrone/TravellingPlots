package com.blutkrone.travellingplots.TravellingPlotV3.Command;

import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.TTravellingPlot;
import com.blutkrone.travellingplots.TravellingPlots;
import com.blutkrone.travellingplots.Util.CommandHandling.BranchingCommandHandler.LeafExecutor;
import com.blutkrone.travellingplots.Util.CommandHandling.BranchingCommandHandler.SignatureVerification;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Exit extends LeafExecutor {

    private final TravellingPlots travellingPlots;

    public Exit(TravellingPlots travellingPlots, boolean isPlayerExclusive, boolean isHidden) {
        super(isPlayerExclusive, isHidden, new SignatureVerification());
        this.travellingPlots = travellingPlots;
    }

    @Override
    public BaseComponent[] usage() {
        return TextComponent.fromLegacyText("No arguments required");
    }

    @Override
    public BaseComponent[] help() {
        return TextComponent.fromLegacyText("Teleports you to the corner stone of the plot.");
    }

    @Override
    public String permissionNode() {
        return "tplot.exit";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        TTravellingPlot registeredPlotBy = travellingPlots.getTravellingPlotHandler().getRegisteredPlotBy(((Player) sender).getLocation());
        if (registeredPlotBy == null) {
            sender.sendMessage(ChatColor.RED + "Cannot use 'exit' since you aren't on a plot!");
        } else {
            ((Player) sender).teleport(registeredPlotBy.getAnchorLocation().clone().add(0, 1, 0));
        }

        return false;
    }
}
