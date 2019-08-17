package com.blutkrone.travellingplots.TravellingPlotV3.Implemented.Serializable;

import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.IBuildInstruction;
import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.Collection.BuildInstructionQueue;
import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Util.BlockStateLookup;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import java.io.Serializable;

public class TBuildInstruction implements IBuildInstruction, Serializable {

    private int x;
    private int y;
    private int z;
    private Material material;
    private String blockData;
    private byte[] blockstate;
    private transient BlockData cachedData;

    private TBuildInstruction() {

    }

    public TBuildInstruction(int x, int y, int z, Material material, BlockData blockData, byte[] blockstate) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.material = material;
        this.blockData = blockData.getAsString();
        this.cachedData = blockData;
        this.blockstate = blockstate;
    }

    public static BuildInstructionQueue convertFromSnapshot(ChunkSnapshot chunkSnapshot, BlockStateLookup lookup) {
        BuildInstructionQueue instructionQueue = new BuildInstructionQueue();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 256; y++) {
                    instructionQueue.add(new TBuildInstruction(x, y, z, chunkSnapshot.getBlockType(x, y, z), chunkSnapshot.getBlockData(x, y, z), lookup.getBlockStateFor(x, y, z)));
                }
            }
        }
        return instructionQueue;
    }

    public static BuildInstructionQueue convertFromChunk(Chunk chunk, BlockStateLookup lookup) {
        BuildInstructionQueue instructionQueue = new BuildInstructionQueue();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 256; y++) {
                    instructionQueue.add(new TBuildInstruction(x, y, z, chunk.getBlock(x, y, z).getType(), chunk.getBlock(x, y, z).getBlockData(), lookup.getBlockStateFor(x, y, z)));
                }
            }
        }
        return instructionQueue;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public BlockData getBlockData() {
        if (cachedData == null) cachedData = Bukkit.createBlockData(blockData);
        return cachedData;
    }

    @Override
    public IBuildInstruction createCloneAt(int x, int y, int z) {
        return new TBuildInstruction(x, y, z, getMaterial(), getBlockData(), getBlockState());
    }

    @Override
    public byte[] getBlockState() {
        return blockstate;
    }
}
