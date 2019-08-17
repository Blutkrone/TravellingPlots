package com.blutkrone.travellingplots.TravellingPlotV3.Implemented;

import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.IBuildInstruction;
import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.IWorldOperationProgress;
import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.IWorldOperator;
import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.Collection.Pair;
import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.AbstractSerializer;
import com.blutkrone.travellingplots.Util.ClassFinder;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

public class TWorldOperator implements IWorldOperator {

    private final static Map<Class<? extends BlockState>, AbstractSerializer> serializerMap = new HashMap<>();
    private final World world;
    private final Object LOCK = new Object();
    private final NavigableMap<Pair<Integer, Integer>, LinkedBlockingQueue<OperationWrapper>> chunkedInstructions = new TreeMap<>(Comparator.comparingInt((Pair<Integer, Integer> o) -> o.first).thenComparingInt(o -> o.second));

    public TWorldOperator(World world, Plugin plugin, TIOHandler ioHandler) {
        this.world = world;

        new BukkitRunnable() {
            @Override
            public void run() {
                commitQueueBy(16 * 16 * 48, false);
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

    public static void initialize(TIOHandler ioHandler) {
        serializerMap.put(org.bukkit.block.Banner.class, new com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Banner(ioHandler));
        serializerMap.put(org.bukkit.block.Beacon.class, new com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Beacon(ioHandler));
        serializerMap.put(org.bukkit.block.BrewingStand.class, new com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.BrewingStand(ioHandler));
        serializerMap.put(org.bukkit.block.Chest.class, new com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Chest(ioHandler));
        serializerMap.put(org.bukkit.block.CommandBlock.class, new com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.CommandBlock(ioHandler));
        serializerMap.put(org.bukkit.block.CreatureSpawner.class, new com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.CreatureSpawner(ioHandler));
        serializerMap.put(org.bukkit.block.Dispenser.class, new com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Dispenser(ioHandler));
        serializerMap.put(org.bukkit.block.Dropper.class, new com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Dropper(ioHandler));
        serializerMap.put(org.bukkit.block.EndGateway.class, new com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.EndGateway(ioHandler));
        serializerMap.put(org.bukkit.block.FlowerPot.class, new com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.FlowerPot(ioHandler));
        serializerMap.put(org.bukkit.block.Furnace.class, new com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Furnace(ioHandler));
        serializerMap.put(org.bukkit.block.Jukebox.class, new com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Jukebox(ioHandler));
        serializerMap.put(org.bukkit.block.NoteBlock.class, new com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.NoteBlock(ioHandler));
        serializerMap.put(org.bukkit.block.ShulkerBox.class, new com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.ShulkerBox(ioHandler));
        serializerMap.put(org.bukkit.block.Sign.class, new com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Sign(ioHandler));
        serializerMap.put(org.bukkit.block.Skull.class, new com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Skull(ioHandler));

        for (Map.Entry<Class<? extends BlockState>, AbstractSerializer> baseClazz : new HashSet<>(serializerMap.entrySet())) {
            Set collection = new ClassFinder(baseClazz.getKey(), "org.bukkit.craftbukkit").getClasses();
            System.out.println("Linking " + baseClazz.getKey() + " to " + collection);
            for (Object o : collection) {
                if (o instanceof Class) {
                    serializerMap.put(((Class) o), baseClazz.getValue());
                } else {
                    try {
                        serializerMap.put((Class) Class.forName(o.toString()), baseClazz.getValue());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void commitQueueBy(int instructionsToCommit, boolean finishChunkIfStarted) {
        synchronized (LOCK) {
            Map.Entry<Pair<Integer, Integer>, LinkedBlockingQueue<OperationWrapper>> entry = chunkedInstructions.pollFirstEntry();

            // ensure we have operations queried.
            if (entry == null) return;

            // ensure that the polled section is loaded
            Chunk chunk = world.getChunkAt(entry.getKey().first, entry.getKey().second);
            boolean wasLoadedBefore = chunk.isLoaded();
            if (!wasLoadedBefore) chunk.load(true);

            // work off our operations.
            if (instructionsToCommit < 0) {
                while (true) {
                    OperationWrapper poll = entry.getValue().poll();
                    if (poll == null) {
                        // we finished this queue, start working off the next one.
                        entry = chunkedInstructions.pollFirstEntry();
                        // unload the previous chunk
                        if (!wasLoadedBefore) chunk.unload(true);

                        if (entry == null) {
                            // we finished all operations, nothing left to work!
                            return;
                        } else {
                            // we finished current operations, new ones waiting!
                            chunk = world.getChunkAt(entry.getKey().first, entry.getKey().second);
                            wasLoadedBefore = chunk.isLoaded();
                            if (!wasLoadedBefore) chunk.load(true);
                            poll = entry.getValue().poll();
                        }
                    }

                    // apply the instructions on the world.
                    Block blockAt = world.getBlockAt(poll.getInstruction().getX(), poll.getInstruction().getY(), poll.getInstruction().getZ());
                    poll.getInstruction().applyOnBlock(blockAt, this);
                    poll.getTracker().completeOperation();
                }
            } else {
                for (int i = 0; i < instructionsToCommit; i++) {
                    OperationWrapper poll = entry.getValue().poll();
                    if (poll == null) {
                        // we finished this queue, start working off the next one.
                        entry = chunkedInstructions.pollFirstEntry();
                        // unload the previous chunk
                        if (!wasLoadedBefore) chunk.unload(true);

                        if (entry == null) {
                            // we finished all operations, nothing left to work!
                            return;
                        } else {
                            // we finished current operations, new ones waiting!
                            chunk = world.getChunkAt(entry.getKey().first, entry.getKey().second);
                            wasLoadedBefore = chunk.isLoaded();
                            if (!wasLoadedBefore) chunk.load(true);
                            poll = entry.getValue().poll();
                        }
                    }

                    // apply the instructions on the world.
                    Block blockAt = world.getBlockAt(poll.getInstruction().getX(), poll.getInstruction().getY(), poll.getInstruction().getZ());
                    poll.getInstruction().applyOnBlock(blockAt, this);
                    poll.getTracker().completeOperation();
                }
            }

            // if we didn't finish query us to the start.
            if (!entry.getValue().isEmpty()) {
                chunkedInstructions.put(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public void loadBlockState(BlockState targetBlockState, byte[] blockstate) {
        AbstractSerializer abstractSerializer = serializerMap.get(targetBlockState.getClass());
        if (abstractSerializer == null) {
            Bukkit.getLogger().warning("Skipped loading " + targetBlockState.getClass() + " since no handler has been registered.");
            return;
        }

        try {
            abstractSerializer.serialize(targetBlockState, blockstate);
        } catch (Exception e) {
            Bukkit.getLogger().severe("Failed loading of " + targetBlockState.getClass() + " due to an invalid file protocol.");
        }
    }

    @Override
    public byte[] saveBlockState(BlockState targetBlockState) {
        AbstractSerializer abstractSerializer = serializerMap.get(targetBlockState.getClass());
        if (abstractSerializer == null) {
            Bukkit.getLogger().warning("Skipped saving " + targetBlockState.getClass() + " since no handler has been registered.");
            return null;
        }
        return abstractSerializer.deserialize(targetBlockState);
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public IWorldOperationProgress queueIntoApproximateChunk(Collection<? extends IBuildInstruction> buildInstruction) {
        TWorldOperationProgress tracker = new TWorldOperationProgress(buildInstruction.size());
        buildInstruction.parallelStream().forEach((instruction) -> {
            int x = instruction.getX() / 16, z = instruction.getZ() / 16;

            synchronized (LOCK) {
                LinkedBlockingQueue<OperationWrapper> operationWrappers = chunkedInstructions.computeIfAbsent(Pair.create(x, z), (k) -> new LinkedBlockingQueue<>());
                operationWrappers.add(new OperationWrapper() {
                    @Override
                    public IBuildInstruction getInstruction() {
                        return instruction;
                    }

                    @Override
                    public TWorldOperationProgress getTracker() {
                        return tracker;
                    }
                });
            }
        });
        return tracker;
    }

    @Override
    public IWorldOperationProgress queueIntoApproximateChunk(Collection<? extends IBuildInstruction> buildInstruction, Function<IBuildInstruction, IBuildInstruction> lazyOperation) {
        TWorldOperationProgress tracker = new TWorldOperationProgress(buildInstruction.size());
        buildInstruction.parallelStream().forEach((instruction) -> {
            int x = instruction.getX() / 16, z = instruction.getZ() / 16;

            synchronized (LOCK) {
                LinkedBlockingQueue<OperationWrapper> operationWrappers = chunkedInstructions.computeIfAbsent(Pair.create(x, z), (k) -> new LinkedBlockingQueue<>());
                operationWrappers.add(new OperationWrapper() {
                    @Override
                    public IBuildInstruction getInstruction() {
                        return lazyOperation.apply(instruction);
                    }

                    @Override
                    public TWorldOperationProgress getTracker() {
                        return tracker;
                    }
                });
            }
        });
        return tracker;
    }

    private interface OperationWrapper {
        IBuildInstruction getInstruction();

        TWorldOperationProgress getTracker();
    }
}
