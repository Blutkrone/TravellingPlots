package com.blutkrone.travellingplots.TravellingPlotV3.Implemented;

import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.IWorldOperationProgress;

public class TWorldOperationProgress implements IWorldOperationProgress {

    private final int totalOperations;
    private final long started;
    private int operation = 0;

    public TWorldOperationProgress(int totalOperations) {
        this.totalOperations = totalOperations;
        this.started = System.currentTimeMillis();
    }

    public synchronized void completeOperation() {
        operation++;
    }

    @Override
    public synchronized long getStartedTimeStamp() {
        return started;
    }

    @Override
    public synchronized int finishedOperations() {
        return operation;
    }

    @Override
    public synchronized int totalOperations() {
        return totalOperations;
    }
}
