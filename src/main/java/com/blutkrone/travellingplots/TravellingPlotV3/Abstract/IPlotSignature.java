package com.blutkrone.travellingplots.TravellingPlotV3.Abstract;

import java.util.Set;
import java.util.UUID;

public interface IPlotSignature {
    Set<UUID> allowedToInteract();

    /**
     * Requires to be serialized.
     */
    int yOffset();
}
