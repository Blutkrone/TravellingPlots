package com.blutkrone.travellingplots.TravellingPlotV3.Tasks.Handler;

import java.util.ArrayList;
import java.util.List;

public class TaskHandler {
    private List<TaskWrapper> currentTask = new ArrayList<>();

    public TaskHandler() {

    }

    public void queue(TaskWrapper task) {
        currentTask.add(task);
    }

    public void clearEmergencyQueue() {
        for (TaskWrapper taskWrapper : currentTask) {
            if (taskWrapper.isEmergencyTask()) {
                taskWrapper.get();
            }
        }
    }

    public void tick() {
        if (!currentTask.isEmpty()) {
            if (currentTask.get(0).get().isFinished()) {
                currentTask.get(0).get().applyCallback();
                currentTask.remove(0);
            }
        }
    }

    public boolean isIdle() {
        return currentTask.isEmpty();
    }
}
