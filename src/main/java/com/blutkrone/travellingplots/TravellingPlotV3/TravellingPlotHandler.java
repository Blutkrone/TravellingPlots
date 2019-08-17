package com.blutkrone.travellingplots.TravellingPlotV3;

import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.IOHandler;
import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.IPlotSignature;
import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.ITravellingPlot;
import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.IWorldOperator;
import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.Collection.Pair;
import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.Serializable.TPlotSignature;
import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.TIOHandler;
import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.TTravellingPlot;
import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.TWorldOperator;
import com.blutkrone.travellingplots.TravellingPlots;
import com.google.common.collect.Sets;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.util.*;

public class TravellingPlotHandler {

    private final TravellingPlots plugin;
    private final TIOHandler ioHandler;
    private final Map<World, IWorldOperator> operators = new HashMap<>();
    private final Map<Location, TTravellingPlot> plots = new HashMap<>();
    private final Map<UUID, TTravellingPlot> lastplot = new HashMap<>();
    private final Map<World, Map<Pair<Integer, Integer>, TTravellingPlot>> trackedPlots = new HashMap<>();
    private final Set<String> plotWorlds = new HashSet<>();

    public TravellingPlotHandler(TravellingPlots plugin) {
        this.plugin = plugin;
        this.ioHandler = new TIOHandler(plugin, this);
        TWorldOperator.initialize(ioHandler);
    }

    public TTravellingPlot getLastPlotOf(UUID owner) {
        return lastplot.get(owner);
    }

    public void setLastPlotOf(UUID owner, TTravellingPlot plot) {
        if (plot == null) {
            lastplot.remove(owner);
        } else lastplot.put(owner, plot);
    }

    public TTravellingPlot getRegisteredPlotBy(Location location) {
        return plots.get(location);
    }

    public TTravellingPlot getRegisteredPlotBy(Chunk location) {
        Map<Pair<Integer, Integer>, TTravellingPlot> pairTTravellingPlotMap = trackedPlots.get(location.getWorld());
        if (pairTTravellingPlotMap == null) return null;
        return pairTTravellingPlotMap.get(Pair.create(location.getX(), location.getZ()));
    }

    public void registerPlot(Location anchor, TTravellingPlot plot) {
        plots.put(anchor, plot);
        plotWorlds.add(anchor.getWorld().getName());
        Map<Pair<Integer, Integer>, TTravellingPlot> pairTTravellingPlotMap = trackedPlots.computeIfAbsent(anchor.getWorld(), (k) -> new HashMap<>());
        for (Chunk chunk : plot.getLinkedChunks()) {
            pairTTravellingPlotMap.put(Pair.create(chunk.getX(), chunk.getZ()), plot);
        }
    }

    /**
     * Representing plugin
     *
     * @return the plugin which is representing the instance.
     */
    public TravellingPlots getPlugin() {
        return plugin;
    }

    /**
     * The IOHandler is responsible for efficient file reading
     * and serializing.
     *
     * @return the world handler we are dealing with.
     */
    public IOHandler getIOHandler() {
        return ioHandler;
    }

    /**
     * Create a signature instance for the given plot.
     *
     * @param offlinePlayer  who is owner of the plot
     * @param travellingPlot the plot which we currently are in possession of.
     * @return the signature which was generated.
     */
    public IPlotSignature makeSignature(OfflinePlayer offlinePlayer, ITravellingPlot travellingPlot) {
        return new TPlotSignature(Sets.newHashSet(offlinePlayer.getUniqueId()), travellingPlot.getAnchorLocation().getBlockY());
    }

    /**
     * The world operator is responsible for minimizing main thread
     * strain and apply changes at a quick pace.
     *
     * @param world the world whose world operator we want
     * @return the relevant world operator for the given world
     */
    public IWorldOperator getWorldOperator(World world) {
        return operators.computeIfAbsent(world, k -> new TWorldOperator(world, plugin, ioHandler));
    }

    public Unsafe unsafe() {
        return new Unsafe();
    }

    public class Unsafe {
        public Map<Location, TTravellingPlot> getPlots() {
            return plots;
        }

        public Map<UUID, TTravellingPlot> getLastplot() {
            return lastplot;
        }

        public Map<World, IWorldOperator> getOperators() {
            return operators;
        }

        public Map<World, Map<Pair<Integer, Integer>, TTravellingPlot>> getTrackedPlots() {
            return trackedPlots;
        }

        public TIOHandler getIoHandler() {
            return ioHandler;
        }
    }
}
