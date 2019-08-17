package com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer;

import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.TIOHandler;
import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Data.EndGatewayData;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class EndGateway extends AbstractSerializer<org.bukkit.block.EndGateway> {
    public EndGateway(TIOHandler ioHandler) {
        super(ioHandler);
    }

    @Override
    public Class<org.bukkit.block.EndGateway> getTargetClass() {
        return org.bukkit.block.EndGateway.class;
    }

    @Override
    public byte[] deserialize(org.bukkit.block.EndGateway state) {
        EndGatewayData data = new EndGatewayData();
        data.age = state.getAge();
        data.exact = state.isExactTeleport();
        if (state.getExitLocation() != null) {
            data.world = state.getExitLocation().getWorld().getName();
            data.x = state.getExitLocation().getX();
            data.y = state.getExitLocation().getY();
            data.z = state.getExitLocation().getZ();
        }
        return new byte[0];
    }

    @Override
    public void serialize(org.bukkit.block.EndGateway state, byte[] deserialized) throws Exception {
        EndGatewayData data = loadData(deserialized, EndGatewayData.class);
        state.setAge(data.age);
        state.setExactTeleport(data.exact);
        if (data.world != null) {
            state.setExitLocation(new Location(Bukkit.getWorld(data.world), data.x, data.y, data.z));
        }
    }

}
