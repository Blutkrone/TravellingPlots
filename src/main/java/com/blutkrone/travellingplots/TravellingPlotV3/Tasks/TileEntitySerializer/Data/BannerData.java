package com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Data;

import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.AbstractData;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.PatternType;

public class BannerData extends AbstractData {
    public DyeColor bcolor;
    public PatternType[] ptype;
    public DyeColor[] pcolor;
}
