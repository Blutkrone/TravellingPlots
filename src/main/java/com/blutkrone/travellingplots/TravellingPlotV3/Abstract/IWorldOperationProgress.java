package com.blutkrone.travellingplots.TravellingPlotV3.Abstract;

public interface IWorldOperationProgress {

    /**
     * The time stamp at which the operation has been queried.
     *
     * @return the time stamp at which we started.
     */
    long getStartedTimeStamp();

    /**
     * The operations which were finished already.
     *
     * @return count of finished operations.
     */
    int finishedOperations();

    /**
     * The operations which are expected in total
     *
     * @return total of operations making up the
     * queried task.
     */
    int totalOperations();
}
