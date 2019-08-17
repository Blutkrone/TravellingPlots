package com.blutkrone.travellingplots.Util.CommandHandling.DefaultExecutors;

import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.Collection.Pair;
import com.blutkrone.travellingplots.Util.CommandHandling.BranchingCommandHandler.BranchingExecutor;
import com.blutkrone.travellingplots.Util.CommandHandling.BranchingCommandHandler.LeafExecutor;
import com.blutkrone.travellingplots.Util.CommandHandling.BranchingCommandHandler.SignatureVerification;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class HelpHandler extends LeafExecutor {

    private final BranchingExecutor owner;
    private final String root;

    public HelpHandler(BranchingExecutor owner, String root) {
        super(false, false, new SignatureVerification());
        this.owner = owner;
        this.root = root;
    }

    @Override
    public BaseComponent[] usage() {
        return new ComponentBuilder("<none>").create();
    }

    @Override
    public BaseComponent[] help() {
        return new ComponentBuilder("Display the command listing").create();
    }

    @Override
    public String permissionNode() {
        return "tplot.help";
    }

    @Override
    public BranchingExecutor getParent() {
        return owner;
    }

    private String describePathUpwards(LeafExecutor destination) {
        if (destination.getParent() != null) {
            for (Map.Entry<String, List<Pair<Integer, LeafExecutor>>> leaflets : destination.getParent().leaf.entrySet()) {
                if (leaflets.getValue().stream().anyMatch(o -> o.getValue() == destination)) {
                    return describePathUpwards(destination.getParent()) + " " + leaflets.getKey();
                }
            }
        }

        return "";
    }

    private String describePathUpwards(BranchingExecutor destination) {
        if (destination.getParent() != null) {
            for (Map.Entry<String, BranchingExecutor> branches : destination.getParent().branch.entrySet()) {
                if (branches.getValue() == destination) {
                    return describePathUpwards(destination.getParent()) + " " + branches.getKey();
                }
            }
        }

        return "";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        for (Pair<Integer, LeafExecutor> LE : owner.getLeafs().stream().filter(e -> !e.getValue().hide()).collect(Collectors.toList())) {
            if (!(LE.getValue().verifyType(sender) && LE.getValue().verifyPermission(sender))) continue;
            String path = "";
            if (LE.getValue().getParent() != null) {
                path = describePathUpwards(LE.getValue());
            }

            List<BaseComponent> BC = new ArrayList<>();
            for (int i = 0; i < LE.getValue().usage().length; i++) {
                BC.add(new ComponentBuilder(String.format("/%s" + path + " ", command.getName())).color(ChatColor.GOLD).create()[0]);

                BC.add(LE.getValue().usage()[i].duplicate());
                // Manually overwrite color and hover event.
                BC.get(BC.size() - 1).setColor(ChatColor.YELLOW);
                BC.get(BC.size() - 1).setHoverEvent(
                        new HoverEvent(HoverEvent.Action.SHOW_TEXT, LE.getValue().help())
                );
            }

            if (sender instanceof Player) {
                ((Player) sender).spigot().sendMessage((BaseComponent[]) BC.toArray(new BaseComponent[0]));
            } else if (sender instanceof ConsoleCommandSender) {
                Bukkit.getServer().getConsoleSender()
                        .sendMessage(TextComponent.toLegacyText((BaseComponent[]) BC.toArray(new BaseComponent[0])));
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}