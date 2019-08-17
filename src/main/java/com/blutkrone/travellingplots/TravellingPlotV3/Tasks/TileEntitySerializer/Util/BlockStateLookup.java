package com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Util;

public interface BlockStateLookup {
    byte[] getBlockStateFor(int x, int y, int z);
}
