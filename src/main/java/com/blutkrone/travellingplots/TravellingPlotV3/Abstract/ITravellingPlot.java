package com.blutkrone.travellingplots.TravellingPlotV3.Abstract;

import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.IPendingTask;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ITravellingPlot {

    /**
     * The unique identifier for this specific plot
     *
     * @return an id which uniquely identifies this plot.
     */
    UUID getPlotUUID();

    /**
     * The position at which the "anchor" of the plot is
     *
     * @return a block which is used to calculate the
     * relative the y-alignment of the plot.
     */
    Location getAnchorLocation();

    /**
     * If applicable who owns this plot
     *
     * @return the owner of the plot, if there is any
     */
    Optional<OfflinePlayer> getCurrentOwner();

    /**
     * Binds a new owner to this plot
     *
     * @param player new owner, null if we want to release the plot.
     */
    void bindToOwner(OfflinePlayer player);

    /**
     * The chunks which are backing this plot.
     *
     * @return the backing chunks for this plot.
     */
    Collection<Chunk> getLinkedChunks();

    /**
     * Loads up the chunks which aren't loaded.
     *
     * @return chunks which weren't loaded before.
     */
    Collection<Chunk> loadIfNotLoaded();

    /**
     * Relative position x of the chunk (in context with
     * how the individual chunks are setup.)
     *
     * @param chunk the chunk we want to position.
     * @return the x position.
     */
    int getRelativeX(Chunk chunk);

    /**
     * Relative position z of the chunk (in context with
     * how the individual chunks are setup.)
     *
     * @param chunk the chunk we want to position.
     * @return the z position.
     */
    int getRelativeZ(Chunk chunk);

    /**
     * Occupy this plot with the given task, serving as a warning
     *
     * @param taskOccupiedWith the task we are occupied with
     */
    void occupyWithTask(IPendingTask taskOccupiedWith);

    /**
     * Check if we are currently occupied
     *
     * @return true if occupied
     */
    boolean isCurrentlyOccupiedWithTask();
}
