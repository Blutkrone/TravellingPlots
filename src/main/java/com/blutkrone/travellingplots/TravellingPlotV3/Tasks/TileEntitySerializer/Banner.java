package com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer;

import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.TIOHandler;
import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.TileEntitySerializer.Data.BannerData;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;

import java.util.List;

public class Banner extends AbstractSerializer<org.bukkit.block.Banner> {
    public Banner(TIOHandler ioHandler) {
        super(ioHandler);
    }

    @Override
    public Class<org.bukkit.block.Banner> getTargetClass() {
        return org.bukkit.block.Banner.class;
    }

    @Override
    public byte[] deserialize(org.bukkit.block.Banner state) {
        BannerData data = new BannerData();
        data.bcolor = state.getBaseColor();
        data.pcolor = new DyeColor[state.getPatterns().size()];
        data.ptype = new PatternType[state.getPatterns().size()];
        List<Pattern> patterns = state.getPatterns();
        for (int i = 0, patternsSize = patterns.size(); i < patternsSize; i++) {
            Pattern pattern = patterns.get(i);
            data.pcolor[i] = pattern.getColor();
            data.ptype[i] = pattern.getPattern();
        }
        return data.getAsByteArray(this);
    }

    @Override
    public void serialize(org.bukkit.block.Banner state, byte[] deserialized) throws Exception {
        BannerData data = loadData(deserialized, BannerData.class);
        state.setBaseColor(data.bcolor);
        DyeColor[] pcolor = data.pcolor;
        for (int i = 0, pcolorLength = pcolor.length; i < pcolorLength; i++) {
            DyeColor dyeColor = pcolor[i];
            PatternType patternType = data.ptype[i];
            state.setPattern(i, new Pattern(dyeColor, patternType));
        }
    }

}
