package com.blutkrone.travellingplots.TravellingPlotV3.Implemented.Serializable;

import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.IPlotSignature;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

public class TPlotSignature implements IPlotSignature, Serializable {

    private transient Set<UUID> allowedToInteract;
    private int yOffset;

    private TPlotSignature() {

    }

    public TPlotSignature(Set<UUID> allowedToInteract, int yOffset) {
        this.allowedToInteract = allowedToInteract;
        this.yOffset = yOffset;
    }

    @Override
    public Set<UUID> allowedToInteract() {
        return allowedToInteract;
    }

    @Override
    public int yOffset() {
        return yOffset;
    }
}
