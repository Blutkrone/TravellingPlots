package com.blutkrone.travellingplots.TravellingPlotV3.Abstract;

import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.io.IOException;
import java.util.AbstractQueue;
import java.util.Optional;

public interface IOHandler<T> {

    /**
     * The unit which handles the serialization.
     *
     * @return the serializer implementation.
     */
    T getSerializer();

    /**
     * Extract build instructions from the given file.
     *
     * @param file the file we are reading the build instructions from
     * @return a queue representing the build instructions contained
     * within the target file
     */
    AbstractQueue<? extends IBuildInstruction> readInstructions(File file) throws IOException;

    /**
     * Read the signature of the given plot entry, if no signature
     * is present returns an empty conditional.
     *
     * @param offlinePlayer whose signature are we trying to read
     * @return the plot signature (if applicable)
     */
    Optional<IPlotSignature> readSignature(OfflinePlayer offlinePlayer) throws IOException;

    /**
     * Read the instructions backing the player stored plot
     *
     * @param owner who owns the plot
     * @param x     the x chunk of the plot we want to read
     * @param z     the z chunk of the plot we want to read
     * @return a queue representing the build instructions contained
     * within the target file.
     */
    AbstractQueue<? extends IBuildInstruction> readPlotInstructions(OfflinePlayer owner, int x, int z) throws IOException;

    /**
     * Fetch the file which contains the information on how to
     * restore the chunk.
     *
     * @param chunkToRestore the chunk we want to restore
     * @return the file which contains the instructions on how
     * to restore a plot.
     */
    File getRestoringFile(Chunk chunkToRestore) throws IOException;

    void saveSignature(OfflinePlayer offlinePlayer, ITravellingPlot travellingPlot) throws IOException;

    void saveInstructions(AbstractQueue<? extends IBuildInstruction> instructions, File output) throws IOException;

    void saveInstructions(OfflinePlayer offlinePlayer, int x, int z, AbstractQueue<? extends IBuildInstruction> instructions) throws IOException;
}
