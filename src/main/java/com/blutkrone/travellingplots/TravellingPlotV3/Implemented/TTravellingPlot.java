package com.blutkrone.travellingplots.TravellingPlotV3.Implemented;

import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.ITravellingPlot;
import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.Collection.Pair;
import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.IPendingTask;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.*;
import java.util.stream.Collectors;

public class TTravellingPlot implements ITravellingPlot {

    private final UUID uuid;
    private final Location anchor;
    private final Collection<Pair<Integer, Integer>> linkedChunks;

    private OfflinePlayer owner;
    private int minX = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
    private IPendingTask taskOccupiedWith;

    public TTravellingPlot(UUID uuid, Location anchor, Collection<Chunk> linkedChunks) {
        this.uuid = uuid;
        this.anchor = anchor;
        this.linkedChunks = Collections.unmodifiableSet(linkedChunks.parallelStream().map(o -> Pair.create(o.getX(), o.getZ())).collect(Collectors.toSet()));

        for (Chunk linkedChunk : linkedChunks) {
            minX = Math.min(minX, linkedChunk.getX());
            minZ = Math.min(minZ, linkedChunk.getZ());
        }
    }

    @Override
    public UUID getPlotUUID() {
        return uuid;
    }

    @Override
    public Location getAnchorLocation() {
        return anchor;
    }

    @Override
    public Optional<OfflinePlayer> getCurrentOwner() {
        return Optional.ofNullable(owner);
    }

    @Override
    public void bindToOwner(OfflinePlayer player) {
        this.owner = player;
    }

    @Override
    public Collection<Chunk> getLinkedChunks() {
        return linkedChunks.stream().map(o -> Objects.requireNonNull(anchor.getWorld()).getChunkAt(o.first, o.second)).collect(Collectors.toSet());
    }

    @Override
    public Collection<Chunk> loadIfNotLoaded() {
        Set<Chunk> notloaded = new HashSet<>();
        for (Chunk chunk : getLinkedChunks()) {
            if (!chunk.isLoaded()) {
                chunk.load(true);
                notloaded.add(chunk);
            }
        }
        return notloaded;
    }

    @Override
    public int getRelativeX(Chunk chunk) {
        return chunk.getX() - minX;
    }

    @Override
    public int getRelativeZ(Chunk chunk) {
        return chunk.getZ() - minZ;
    }

    @Override
    public void occupyWithTask(IPendingTask taskOccupiedWith) {
        this.taskOccupiedWith = taskOccupiedWith;
    }

    @Override
    public boolean isCurrentlyOccupiedWithTask() {
        return taskOccupiedWith != null && !taskOccupiedWith.isFinished();
    }
}
