package com.blutkrone.travellingplots.TravellingPlotV3.Command;

import com.blutkrone.travellingplots.TravellingPlots;
import com.blutkrone.travellingplots.Util.CommandHandling.BranchingCommandHandler.LeafExecutor;
import com.blutkrone.travellingplots.Util.CommandHandling.BranchingCommandHandler.SignatureVerification;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class Validate extends LeafExecutor {

    private final TravellingPlots travellingPlots;

    public Validate(TravellingPlots travellingPlots, boolean isPlayerExclusive, boolean isHidden, SignatureVerification verification) {
        super(isPlayerExclusive, isHidden, verification);
        this.travellingPlots = travellingPlots;
    }

    @Override
    public BaseComponent[] usage() {
        return TextComponent.fromLegacyText("No arguments necessary.");
    }

    @Override
    public BaseComponent[] help() {
        return TextComponent.fromLegacyText("Validates all plots on the server, also recomputes the " +
                "configuration elements. If the plot sign is missing, we will re-create it.");
    }

    @Override
    public String permissionNode() {
        return "tplot.admin";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }
}
