package com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer;

import com.blutkrone.travellingplots.ItemStackSerializer;
import org.bukkit.block.Container;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.io.Serializable;

public class AbstractData implements Serializable {

    public byte[] inventory;

    public void saveInventory(Container container) {
        inventory = ItemStackSerializer.toByteArray(container.getSnapshotInventory().getContents());
    }

    public void loadInventory(Container container) {
        try {
            container.getSnapshotInventory().setContents(ItemStackSerializer.fromByteArray(inventory));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ItemStack getItemOrElse(byte[] data, ItemStack fallback) {
        try {
            ItemStack[] itemStacks = ItemStackSerializer.fromByteArray(data);
            if (itemStacks.length == 0) return fallback;
            if (itemStacks[0] == null) return fallback;
            return itemStacks[0];
        } catch (IOException e) {
            return fallback;
        }
    }

    public byte[] getAsByteArray(AbstractSerializer abstractSerializer) {
        return abstractSerializer.getIoHandler().getSerializer().asByteArray(AbstractData.this);
    }
}
