package com.blutkrone.travellingplots.TravellingPlotV3.Tasks.Handler;

import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.EmergencyTask.IEmergencyTask;
import com.blutkrone.travellingplots.TravellingPlotV3.Tasks.IPendingTask;

import java.util.function.Supplier;

public class TaskWrapper<T extends IPendingTask> {
    private final Supplier<T> taskSupplier;
    private IPendingTask fetched;
    private boolean isEmergencyTask;

    public TaskWrapper(Class<T> clazz, Supplier<T> taskSupplier) {
        this.isEmergencyTask = IEmergencyTask.class.isAssignableFrom(clazz);
        this.taskSupplier = taskSupplier;
    }

    public boolean isEmergencyTask() {
        return isEmergencyTask;
    }

    public IPendingTask get() {
        if (fetched == null) fetched = taskSupplier.get();
        return fetched;
    }
}
