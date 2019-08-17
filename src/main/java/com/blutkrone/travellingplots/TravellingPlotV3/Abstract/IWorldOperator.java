package com.blutkrone.travellingplots.TravellingPlotV3.Abstract;

import org.bukkit.World;
import org.bukkit.block.BlockState;

import java.util.Collection;
import java.util.function.Function;

public interface IWorldOperator {

    /**
     * Commit the changes within the internal query, reflecting them
     * to the world and releasing the memory reserved.
     *
     * @param instructionsToCommit the number of instructions we want to commit before releasing
     *                             the thread we are working on.
     * @param finishChunkIfStarted if we run out of instructions, should we still finish all
     *                             instructions remaining within the chunk?
     */
    void commitQueueBy(int instructionsToCommit, boolean finishChunkIfStarted);

    /**
     * Attempt to convert the byte array to a blockstate and apply it on the target
     *
     * @param targetBlockState who should receive the blockstate
     * @param blockstate       blockstate (null/empty)
     */
    void loadBlockState(BlockState targetBlockState, byte[] blockstate);

    /**
     * Generate a byte array representing a blockstate
     *
     * @param targetBlockState the blockstate we want to represent
     * @return the byte array which represents the blockstate now.
     */
    byte[] saveBlockState(BlockState targetBlockState);

    /**
     * The world backing this operator
     *
     * @return the world we are operating on
     */
    World getWorld();

    /**
     * Queue into the underlying collection, broken up into chunk
     * operations for quicker chunk loading operations.
     *
     * @param buildInstruction the instructions to queue
     * @return the progress container notifying us about how long until we are
     * finished with this given task.
     */
    IWorldOperationProgress queueIntoApproximateChunk(Collection<? extends IBuildInstruction> buildInstruction);

    /**
     * Queue into the underlying collection, broken up into chunk
     * operations for quicker chunk loading operations.
     *
     * @param buildInstruction the instructions to queue
     * @param lazyOperation    an operation which will be applied on every
     *                         instruction prior to when it is actually
     *                         queried.
     * @return the progress container notifying us about how long until we are
     * finished with this given task.
     */
    IWorldOperationProgress queueIntoApproximateChunk(Collection<? extends IBuildInstruction> buildInstruction, Function<IBuildInstruction, IBuildInstruction> lazyOperation);
}
