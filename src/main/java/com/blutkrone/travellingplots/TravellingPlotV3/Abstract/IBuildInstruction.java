package com.blutkrone.travellingplots.TravellingPlotV3.Abstract;

import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.TWorldOperator;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface IBuildInstruction {

    int getX();

    int getY();

    int getZ();

    /**
     * The material representing us.
     *
     * @return the material
     */
    Material getMaterial();

    /**
     * The block data representing the instruction.
     *
     * @return the block data
     */
    BlockData getBlockData();

    /**
     * Create an identical copy of this instruction, but relocate
     * it to the given position.
     *
     * @param x new x coordinate
     * @param y new y coordinate
     * @param z new z coordinate
     * @return the same instruction but relocated to the given position.
     */
    IBuildInstruction createCloneAt(int x, int y, int z);

    /**
     * A collection that might contain a serialized version of
     * the block state, which will be attempted to be recovered
     *
     * @return the byte array representing a potential recoverable block state
     */
    byte[] getBlockState();

    /**
     * Apply the changes contained within the instruction at the given position.
     *
     * @param target         the target we want to apply on, you can expect the chunk
     *                       to be loaded whenever this method is called.
     * @param tWorldOperator
     */
    default void applyOnBlock(Block target, TWorldOperator tWorldOperator) {
        // prevents item eruption, clearing the container before doing something with it.
        if (target.getState() instanceof Container) {
            Container container = ((Container) target.getState());
            Inventory snapshotInventory = container.getSnapshotInventory();
            snapshotInventory.setContents(new ItemStack[snapshotInventory.getContents().length]);
            container.update(true, false);
        }

        target.setType(getMaterial(), false);
        BlockData blockData = getBlockData();
        if (blockData != null) {
            target.setBlockData(blockData, false);

            BlockState blockState = target.getState();
            if (getBlockState() != null) {
                tWorldOperator.loadBlockState(blockState, getBlockState());
            }
            blockState.update(true, false);
        }
    }
}
