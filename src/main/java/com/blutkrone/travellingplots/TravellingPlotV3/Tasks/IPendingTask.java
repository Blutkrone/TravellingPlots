package com.blutkrone.travellingplots.TravellingPlotV3.Tasks;

public interface IPendingTask {

    /**
     * @return true when we finished with the operation this task was created for.
     */
    boolean isFinished();

    /**
     * Executes the callback implementation, if one exists.
     */
    default void applyCallback() {

    }
}
