package com.blutkrone.travellingplots.TravellingPlotV3.Command;

import com.blutkrone.travellingplots.Util.CommandHandling.BranchingCommandHandler.LeafExecutor;
import com.blutkrone.travellingplots.Util.CommandHandling.BranchingCommandHandler.SignatureVerification;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class CreationAssist extends LeafExecutor implements Listener {
    private Map<UUID, Chunk> chunkA = new HashMap<>(), chunkB = new HashMap<>();
    private WeakHashMap<Player, Boolean> noSpecified = new WeakHashMap<>();

    public CreationAssist(Plugin plugin, boolean isPlayerExclusive, boolean isHidden) {
        super(isPlayerExclusive, isHidden, new SignatureVerification(1, 1));
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        new BukkitRunnable() {
            @Override
            public void run() {
                chunkA.forEach((playerUUID, start) -> {
                    Chunk finish = chunkB.get(playerUUID);

                    Player player = Bukkit.getPlayer(playerUUID);
                    if (player == null || finish == null) return;
                    if (!(finish.getWorld() == start.getWorld() && start.getWorld() == player.getWorld()))
                        return;

                    int minX = Math.min(start.getX(), finish.getX()) * 16;
                    int minZ = Math.min(start.getZ(), finish.getZ()) * 16;
                    int maxX = (Math.max(start.getX(), finish.getX()) + 1) * 16;
                    int maxZ = (Math.max(start.getZ(), finish.getZ()) + 1) * 16;

                    for (int x = minX; x <= maxX; x++) {
                        if (!new Location(start.getWorld(), x, player.getLocation().getY() + 1, minZ).getChunk().isLoaded()) {
                            x += 16;
                            continue;
                        }
                        player.spawnParticle(Particle.REDSTONE, new Location(start.getWorld(), x, player.getLocation().getY() + 1, minZ), 1, new Particle.DustOptions(Color.RED, 1));
                        player.spawnParticle(Particle.REDSTONE, new Location(start.getWorld(), x, player.getLocation().getY() + 2, minZ), 5, new Particle.DustOptions(Color.RED, 1));
                        player.spawnParticle(Particle.REDSTONE, new Location(start.getWorld(), x, player.getLocation().getY() + 3, minZ), 1, new Particle.DustOptions(Color.RED, 1));
                    }

                    for (int z = minZ; z <= maxZ; z++) {
                        if (!new Location(start.getWorld(), minX, player.getLocation().getY() + 1, z).getChunk().isLoaded()) {
                            z += 16;
                            continue;
                        }
                        player.spawnParticle(Particle.REDSTONE, new Location(start.getWorld(), minX, player.getLocation().getY() + 1, z), 1, new Particle.DustOptions(Color.RED, 1));
                        player.spawnParticle(Particle.REDSTONE, new Location(start.getWorld(), minX, player.getLocation().getY() + 2, z), 20, new Particle.DustOptions(Color.RED, 1));
                        player.spawnParticle(Particle.REDSTONE, new Location(start.getWorld(), minX, player.getLocation().getY() + 3, z), 5, new Particle.DustOptions(Color.RED, 1));
                    }

                    for (int x = minX; x <= maxX; x++) {
                        if (!new Location(start.getWorld(), x, player.getLocation().getY() + 1, maxZ).getChunk().isLoaded()) {
                            x += 16;
                            continue;
                        }
                        player.spawnParticle(Particle.REDSTONE, new Location(start.getWorld(), x, player.getLocation().getY() + 1, maxZ), 1, new Particle.DustOptions(Color.RED, 1));
                        player.spawnParticle(Particle.REDSTONE, new Location(start.getWorld(), x, player.getLocation().getY() + 2, maxZ), 5, new Particle.DustOptions(Color.RED, 1));
                        player.spawnParticle(Particle.REDSTONE, new Location(start.getWorld(), x, player.getLocation().getY() + 3, maxZ), 1, new Particle.DustOptions(Color.RED, 1));
                    }

                    for (int z = minZ; z <= maxZ; z++) {
                        if (!new Location(start.getWorld(), maxX, player.getLocation().getY() + 1, z).getChunk().isLoaded()) {
                            z += 16;
                            continue;
                        }
                        player.spawnParticle(Particle.REDSTONE, new Location(start.getWorld(), maxX, player.getLocation().getY() + 1, z), 1, new Particle.DustOptions(Color.RED, 1));
                        player.spawnParticle(Particle.REDSTONE, new Location(start.getWorld(), maxX, player.getLocation().getY() + 2, z), 5, new Particle.DustOptions(Color.RED, 1));
                        player.spawnParticle(Particle.REDSTONE, new Location(start.getWorld(), maxX, player.getLocation().getY() + 3, z), 1, new Particle.DustOptions(Color.RED, 1));
                    }
                });
            }
        }.runTaskTimer(plugin, 1L, 20L);
    }

    @Override
    public BaseComponent[] usage() {
        return TextComponent.fromLegacyText("<auto/1/2/cancel>");
    }

    @Override
    public BaseComponent[] help() {
        return new ComponentBuilder("Cornerstone Creation Assistant\n")
                .append("Select the two corners which will make up the plot\n")
                .append("Right click on a sign to make it the cornerstone.\n")
                .create();
    }

    @Override
    public String permissionNode() {
        return "tplot.admin";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args[0].equalsIgnoreCase("auto")) {
            boolean now = noSpecified.getOrDefault(((Player) sender), false);
            if (now) {
                sender.sendMessage(ChatColor.GREEN + "Selected Chunk 1");
                chunkA.put(((Player) sender).getUniqueId(), ((Player) sender).getLocation().getChunk());
            } else {
                sender.sendMessage(ChatColor.GREEN + "Selected Chunk 2");
                chunkB.put(((Player) sender).getUniqueId(), ((Player) sender).getLocation().getChunk());
            }
            noSpecified.put(((Player) sender), !now);
        } else if (args[0].equalsIgnoreCase("1")) {
            sender.sendMessage(ChatColor.GREEN + "Selected Chunk 1");
            chunkA.put(((Player) sender).getUniqueId(), ((Player) sender).getLocation().getChunk());
        } else if (args[0].equalsIgnoreCase("2")) {
            sender.sendMessage(ChatColor.GREEN + "Selected Chunk 2");
            chunkB.put(((Player) sender).getUniqueId(), ((Player) sender).getLocation().getChunk());
        } else if (args[0].equalsIgnoreCase("cancel")) {
            sender.sendMessage(ChatColor.GREEN + "Cleared selection");
            chunkA.remove(((Player) sender).getUniqueId());
            chunkB.remove(((Player) sender).getUniqueId());
        } else {
            return false;
        }

        if (chunkA.containsKey(((Player) sender).getUniqueId()) && chunkB.containsKey(((Player) sender).getUniqueId())) {
            sender.sendMessage(ChatColor.GREEN + "Right click a sign to make it the cornerstone!");
        }

        return true;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void on(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!chunkA.containsKey(e.getPlayer().getUniqueId()) || !chunkB.containsKey(e.getPlayer().getUniqueId()))
            return;
        if (e.getClickedBlock() == null) return;
        if (!(e.getClickedBlock().getState() instanceof Sign)) return;

        // ensure that we select the correct thresholds:
        Sign state = ((Sign) e.getClickedBlock().getState());
        state.setLine(0, "[TPLOT]");
        state.setLine(1, Math.min(chunkA.get(e.getPlayer().getUniqueId()).getX(), chunkB.get(e.getPlayer().getUniqueId()).getX()) + " " + Math.min(chunkA.get(e.getPlayer().getUniqueId()).getZ(), chunkB.get(e.getPlayer().getUniqueId()).getZ()));
        state.setLine(2, Math.max(chunkA.get(e.getPlayer().getUniqueId()).getX(), chunkB.get(e.getPlayer().getUniqueId()).getX()) + " " + Math.max(chunkA.get(e.getPlayer().getUniqueId()).getZ(), chunkB.get(e.getPlayer().getUniqueId()).getZ()));
        state.update(true);

        // prevent accidental removal.
        chunkA.remove(e.getPlayer().getUniqueId());
        chunkB.remove(e.getPlayer().getUniqueId());
    }
}
