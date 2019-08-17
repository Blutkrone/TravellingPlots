package com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer;

import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.TIOHandler;
import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Data.CreatureSpawnerData;

public class CreatureSpawner extends AbstractSerializer<org.bukkit.block.CreatureSpawner> {
    public CreatureSpawner(TIOHandler ioHandler) {
        super(ioHandler);
    }

    @Override
    public Class<org.bukkit.block.CreatureSpawner> getTargetClass() {
        return org.bukkit.block.CreatureSpawner.class;
    }

    @Override
    public byte[] deserialize(org.bukkit.block.CreatureSpawner state) {
        CreatureSpawnerData data = new CreatureSpawnerData();
        data.delay = state.getDelay();
        data.maxnearby = state.getMaxNearbyEntities();
        data.maxdelay = state.getMaxSpawnDelay();
        data.mindelay = state.getMinSpawnDelay();
        data.playerrange = state.getRequiredPlayerRange();
        data.spawnrange = state.getSpawnRange();
        data.type = state.getSpawnedType();
        return data.getAsByteArray(this);
    }

    @Override
    public void serialize(org.bukkit.block.CreatureSpawner state, byte[] deserialized) throws Exception {
        CreatureSpawnerData data = loadData(deserialized, CreatureSpawnerData.class);
        state.setDelay(data.delay);
        state.setMaxNearbyEntities(data.maxnearby);
        state.setMaxSpawnDelay(data.maxdelay);
        state.setMinSpawnDelay(data.mindelay);
        state.setRequiredPlayerRange(data.playerrange);
        state.setSpawnRange(data.spawnrange);
        state.setSpawnedType(data.type);
    }

}
