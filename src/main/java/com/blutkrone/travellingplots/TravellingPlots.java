package com.blutkrone.travellingplots;

import com.blutkrone.travellingplots.TravellingPlotV3.Command.CreationAssist;
import com.blutkrone.travellingplots.TravellingPlotV3.Command.Exit;
import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.TTravellingPlot;
import com.blutkrone.travellingplots.TravellingPlotV3.Security.*;
import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.*;
import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.EmergencyTask.ForceResetTask;
import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.EmergencyTask.ForceSaveTask;
import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.Handler.TaskHandler;
import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.Handler.TaskWrapper;
import com.blutkrone.travellingplots.TravellingPlotV3.TravellingPlotHandler;
import com.blutkrone.travellingplots.Util.CommandHandling.BranchingCommandHandler.BranchingExecutor;
import com.blutkrone.travellingplots.Util.CommandHandling.DefaultExecutors.HelpHandler;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class TravellingPlots extends JavaPlugin implements Listener {

    private static TravellingPlots travellingPlots;
    private final Map<String, Integer> cooldownReductionByPermission = new HashMap<>();
    private final Map<UUID, Integer> reuseCooldown = new HashMap<>();
    private TravellingPlotHandler travellingPlotHandler;
    private SecurityAdapterListener securityAdapterListener;
    private Map<UUID, TaskHandler> taskHandlers = new HashMap<>();
    private WeakHashMap<Player, Long> antiSpamCooldown = new WeakHashMap<>();
    private BranchingExecutor commandRoot;
    private int cooldown;

    public static void log(int level, String message) {
        // StringBuilder messageBuilder = new StringBuilder(message);
        // for (int i = 0; i < level; i++) {
        //     messageBuilder.insert(0, "-");
        // }
        // message = messageBuilder.toString();
        // System.out.println(message);
    }

    public static TravellingPlots inst() {
        return travellingPlots;
    }

    public TravellingPlotHandler getTravellingPlotHandler() {
        return travellingPlotHandler;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        new BukkitRunnable() {
            @Override
            public void run() {
                synchronized (reuseCooldown) {
                    reuseCooldown.entrySet().removeIf(e -> {
                        e.setValue(e.getValue() - 20);
                        return e.getValue() <= 0;
                    });
                }
            }
        }.runTaskTimerAsynchronously(this, 20L, 20L);

        travellingPlots = this;
        travellingPlotHandler = new TravellingPlotHandler(this);
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(securityAdapterListener = new SecurityAdapterListener(this), this);

        securityAdapterListener.getSecurityAdapters().add(new BuildHeightSecurityAdapter(getConfig().getInt("build-height-up", 30), getConfig().getInt("build-height-down", 30)));
        securityAdapterListener.getSecurityAdapters().add(new DisallowModifyingEmptyPlotSecurityAdapter());

        if (getConfig().isConfigurationSection("immunity-by-permission"))
            securityAdapterListener.getSecurityAdapters().add(new BlockedDamageCauses(getConfig().getConfigurationSection("immunity-by-permission")));
        if (!getConfig().getBoolean("allow-spawn-on-plot"))
            securityAdapterListener.getSecurityAdapters().add(new EntityNoSpawnSecurityAdapter());
        if (!getConfig().getBoolean("allow-pvp-on-plot"))
            securityAdapterListener.getSecurityAdapters().add(new NoPVPSecurityAdapter());

        cooldown = 20 * getConfig().getInt("default-reuse-cooldown", 60);
        if (getConfig().isConfigurationSection("cooldown-reduction-permissions")) {
            for (String perm : getConfig().getConfigurationSection("cooldown-reduction-permissions").getKeys(false)) {
                cooldownReductionByPermission.put(perm.replace(" ", "."), 20 * getConfig().getInt("cooldown-reduction-permissions." + perm));
            }
        }

        getCommand("tplot").setExecutor(commandRoot = new BranchingExecutor(null));
        getCommand("tplot").setTabCompleter(commandRoot);

        commandRoot.createLeaf("help", new HelpHandler(commandRoot, "tplot"));
        commandRoot.createLeaf("ca", new CreationAssist(this, true, false));
        commandRoot.createLeaf("exit", new Exit(this, true, false));

        new BukkitRunnable() {
            @Override
            public void run() {
                taskHandlers.entrySet().removeIf(task -> {
                    if (task.getValue().isIdle()) return true;
                    task.getValue().tick();
                    return false;
                });
            }
        }.runTaskTimer(this, 1L, 1L);

        // load all plots which we saved from the last iteration
        new BukkitRunnable() {
            @Override
            public void run() {
                ConfigurationSection registered = getConfig().getConfigurationSection("registered");
                if (registered == null) return;
                Map<Location, List<Chunk>> anchorToChunk = new HashMap<>();
                for (String world : registered.getKeys(false)) {
                    World targetWorld = Bukkit.getWorld(world);
                    if (targetWorld == null) {
                        Bukkit.getLogger().severe("WORLD " + world + " COULD NOT BE FOUND!");
                        continue;
                    }

                    ConfigurationSection subsection = registered.getConfigurationSection(world);
                    if (subsection == null) throw new IllegalArgumentException("Corrupted Configuration File Syntax!");
                    for (String chunkPos : subsection.getKeys(false)) {
                        Chunk chunkAt = targetWorld.getChunkAt(Integer.parseInt(chunkPos.split(" ")[0]), Integer.parseInt(chunkPos.split(" ")[1]));
                        Location location = Objects.requireNonNull(subsection.getVector(chunkPos)).toLocation(targetWorld);
                        anchorToChunk.computeIfAbsent(location, (k) -> new ArrayList<>()).add(chunkAt);
                    }
                }

                anchorToChunk.forEach((anchor, chunks) -> {
                    if (anchor.getChunk().isLoaded()) {
                        if (anchor.getBlock().getState() instanceof Sign && ((Sign) anchor.getBlock().getState()).getLine(0).equalsIgnoreCase("[TPLOT]")) {
                            getTravellingPlotHandler().registerPlot(anchor, new TTravellingPlot(UUID.randomUUID(), anchor, chunks));
                        } else Bukkit.getLogger().warning("Plot at " + anchor + " is missing corner stone sign!");
                    } else {
                        anchor.getChunk().load(true);
                        if (anchor.getBlock().getState() instanceof Sign && ((Sign) anchor.getBlock().getState()).getLine(0).equalsIgnoreCase("[TPLOT]")) {
                            getTravellingPlotHandler().registerPlot(anchor, new TTravellingPlot(UUID.randomUUID(), anchor, chunks));
                        } else Bukkit.getLogger().warning("Plot at " + anchor + " is missing corner stone sign!");
                        anchor.getChunk().unload(false);
                    }
                });
            }
        }.runTaskLater(this, 10L);
    }

    @Override
    public void onDisable() {
        // all currently running plots are expected to be force cleared, query every cleanup now.
        getTravellingPlotHandler().unsafe().getPlots().values().forEach(activePlot -> {
            if (activePlot.getCurrentOwner().isPresent()) {
                log(0, "Found still active plot with anchor at " + activePlot.getAnchorLocation()
                        + ", proceeding to save and reset . . .");
                taskHandlers.computeIfAbsent(activePlot.getCurrentOwner().get().getUniqueId(), (k) -> new TaskHandler())
                        .queue(new TaskWrapper<>(ForceSaveTask.class, () -> new ForceSaveTask(travellingPlotHandler, activePlot)));
                taskHandlers.computeIfAbsent(activePlot.getCurrentOwner().get().getUniqueId(), (k) -> new TaskHandler())
                        .queue(new TaskWrapper<>(ForceResetTask.class, () -> new ForceResetTask(travellingPlotHandler, activePlot)));
            }
        });

        // force run the emergency queue
        taskHandlers.values().forEach(TaskHandler::clearEmergencyQueue);

        // work off everything we have within our queue
        getTravellingPlotHandler().unsafe().getOperators().values().forEach(operator -> {
            operator.commitQueueBy(-1, true);
        });

        // update the underlying mapping with all activated plots
        getTravellingPlotHandler().unsafe().getPlots().values().forEach(activePlot -> {
            for (Chunk chunk : activePlot.getLinkedChunks()) {
                getConfig().set("registered." + activePlot.getAnchorLocation().getWorld().getName() + "." + chunk.getX() + " " + chunk.getZ(), activePlot.getAnchorLocation().toVector());
            }
        });

        // in case there was a change to the configuration, write it.
        try {
            getConfig().save(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    void on(PlayerQuitEvent e) {
        TTravellingPlot lastPlotOf = getTravellingPlotHandler().getLastPlotOf(e.getPlayer().getUniqueId());
        if (lastPlotOf == null) return;
        TaskHandler taskHandler = taskHandlers.computeIfAbsent(e.getPlayer().getUniqueId(), (k) -> new TaskHandler());

        // teleport everyone who still is on the old out
        taskHandler.queue(new TaskWrapper<>(RescueEntitiesTask.class, () -> new RescueEntitiesTask(travellingPlotHandler, lastPlotOf)));
        // save our old plot
        taskHandler.queue(new TaskWrapper<>(SavePlotTask.class, () -> new SavePlotTask(travellingPlotHandler, lastPlotOf)));
        // clear our old plot
        taskHandler.queue(new TaskWrapper<>(ResetPlotTask.class, () -> new ResetPlotTask(travellingPlotHandler, lastPlotOf)));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    void on(PlayerDeathEvent e) {
        TTravellingPlot lastPlotOf = getTravellingPlotHandler().getLastPlotOf(e.getEntity().getUniqueId());
        if (lastPlotOf == null) return;
        TaskHandler taskHandler = taskHandlers.computeIfAbsent(e.getEntity().getUniqueId(), (k) -> new TaskHandler());

        // teleport everyone who still is on the old out
        taskHandler.queue(new TaskWrapper<>(RescueEntitiesTask.class, () -> new RescueEntitiesTask(travellingPlotHandler, lastPlotOf)));
        // save our old plot
        taskHandler.queue(new TaskWrapper<>(SavePlotTask.class, () -> new SavePlotTask(travellingPlotHandler, lastPlotOf)));
        // clear our old plot
        taskHandler.queue(new TaskWrapper<>(ResetPlotTask.class, () -> new ResetPlotTask(travellingPlotHandler, lastPlotOf)));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    void on(PlayerInteractEvent e) {
        // ensure we are not checking unnecessarily
        if (e.getHand() != EquipmentSlot.HAND || e.getClickedBlock() == null) return;
        // ensure we are checking a travelling plots sign
        if (!(e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
        if (!(e.getClickedBlock().getState() instanceof Sign)) return;
        // ensure we aren't on cooldown (prevents unexpected interactions.)
        if (antiSpamCooldown.getOrDefault(e.getPlayer(), 0L) > System.currentTimeMillis()) return;
        antiSpamCooldown.put(e.getPlayer(), System.currentTimeMillis() + 500L);

        if (travellingPlotHandler.getRegisteredPlotBy(e.getClickedBlock().getLocation()) == null) {
            // no plot has been registered here, register it before we proceed with out operation.
            String[] data = ((Sign) e.getClickedBlock().getState()).getLines();
            if (!data[0].equalsIgnoreCase("[TPLOT]")) return;
            // extract the bounds of the plot
            int startX = Integer.valueOf(data[1].split(" ")[0]);
            int startZ = Integer.valueOf(data[1].split(" ")[1]);
            int finishX = Integer.valueOf(data[2].split(" ")[0]);
            int finishZ = Integer.valueOf(data[2].split(" ")[1]);

            // extract which chunks will belong to this plot.
            List<Chunk> linkedChunks = new ArrayList<>();
            for (int x = startX; x <= finishX; x++) {
                for (int z = startZ; z <= finishZ; z++) {
                    linkedChunks.add(e.getPlayer().getWorld().getChunkAt(x, z));
                }
            }

            // register the plot to the API
            travellingPlotHandler.registerPlot(e.getClickedBlock().getLocation(),
                    new TTravellingPlot(UUID.randomUUID(), e.getClickedBlock().getLocation(), linkedChunks));
        }

        if (!e.getPlayer().hasPermission("tplot.use")) {
            e.getPlayer().hasPermission(ChatColor.RED + "You do not have permission to do this!");
        }

        TaskHandler taskHandler = taskHandlers.computeIfAbsent(e.getPlayer().getUniqueId(), (k) -> new TaskHandler());
        TTravellingPlot newPlot = travellingPlotHandler.getRegisteredPlotBy(e.getClickedBlock().getLocation());

        // make sure we won't snatch away someone else their plot.
        if (newPlot.getCurrentOwner().isPresent()) {
            if (newPlot.getCurrentOwner().get().getUniqueId().equals(e.getPlayer().getUniqueId())) {
                // we only want to unload out plot.
                travellingPlotHandler.setLastPlotOf(e.getPlayer().getUniqueId(), null);

                // teleport everyone who still is on the plot out
                taskHandler.queue(new TaskWrapper<>(RescueEntitiesTask.class, () -> new RescueEntitiesTask(travellingPlotHandler, newPlot)));

                // save our old plot
                taskHandler.queue(new TaskWrapper<>(SavePlotTask.class, () -> new SavePlotTask(travellingPlotHandler, newPlot)));

                // clear our old plot
                taskHandler.queue(new TaskWrapper<>(ResetPlotTask.class, () -> new ResetPlotTask(travellingPlotHandler, newPlot)));
            } else {
                e.getPlayer().sendMessage(ChatColor.RED + "Plot is already occupied!");
            }

            return;
        }

        // make sure that we have no task pending on the new plot
        if (newPlot.isCurrentlyOccupiedWithTask()) {
            e.getPlayer().sendMessage(ChatColor.RED + "Target plot has a task pending!");
            return;
        } else {
            synchronized (reuseCooldown) {
                if (reuseCooldown.containsKey(e.getPlayer().getUniqueId())) {
                    // we are within the reuse cooldown!
                    e.getPlayer().sendMessage(ChatColor.YELLOW + "You can summon your plot in " + (int) Math.ceil(((1D * reuseCooldown.get(e.getPlayer().getUniqueId())) / 20D)) + " seconds!");
                    return;
                } else {
                    int cooldown = this.cooldown;
                    for (Map.Entry<String, Integer> reduction : cooldownReductionByPermission.entrySet()) {
                        if (e.getPlayer().hasPermission(reduction.getKey()))
                            cooldown -= reduction.getValue();
                    }
                    reuseCooldown.put(e.getPlayer().getUniqueId(), cooldown);
                }
            }
        }

        // teleport everyone who still is on the plot out
        taskHandler.queue(new TaskWrapper<>(RescueEntitiesTask.class, () -> new RescueEntitiesTask(travellingPlotHandler, newPlot)));

        // create the backup of our plot first
        taskHandler.queue(new TaskWrapper<>(BackupEmptyPlotTask.class, () -> new BackupEmptyPlotTask(travellingPlotHandler, newPlot)));

        // take care of our old plot first.
        if (travellingPlotHandler.getLastPlotOf(e.getPlayer().getUniqueId()) != null) {
            TTravellingPlot lastPlot = travellingPlotHandler.getLastPlotOf(e.getPlayer().getUniqueId());

            if (lastPlot.isCurrentlyOccupiedWithTask()) {
                e.getPlayer().sendMessage(ChatColor.RED + "Previous plot has a task pending!");
                return;
            }

            // teleport everyone who still is on the old out
            taskHandler.queue(new TaskWrapper<>(RescueEntitiesTask.class, () -> new RescueEntitiesTask(travellingPlotHandler, lastPlot)));
            // save our old plot
            taskHandler.queue(new TaskWrapper<>(SavePlotTask.class, () -> new SavePlotTask(travellingPlotHandler, lastPlot)));
            // clear our old plot
            taskHandler.queue(new TaskWrapper<>(ResetPlotTask.class, () -> new ResetPlotTask(travellingPlotHandler, lastPlot)));
        }

        travellingPlotHandler.setLastPlotOf(e.getPlayer().getUniqueId(), newPlot);
        taskHandler.queue(new TaskWrapper<>(LoadPlotTask.class, () -> new LoadPlotTask(travellingPlotHandler, newPlot, e.getPlayer())));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    void on(SignChangeEvent e) {
        if (ChatColor.stripColor(e.getLine(0)).equalsIgnoreCase("[TPLOT]")) {
            if (!e.getPlayer().hasPermission("tplot.admin")) {
                e.getPlayer().sendMessage(ChatColor.RED + "You do not have the permission to do this.");
                e.setCancelled(true);
            } else {
                e.getPlayer().sendMessage(ChatColor.GREEN + "Make sure to right-click to register! (Consider using '/tplot ca ...' for assisted creation!)");
            }
        }
    }
}
