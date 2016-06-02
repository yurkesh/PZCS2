package edu.kpi.nesteruk.pzcs.planning.processors;

import edu.kpi.nesteruk.pzcs.planning.planner.TaskWithTransfersEstimate;

/**
 * Created by Anatolii Bed on 2016-05-23.
 */
public class ProcessorWithTaskEstimate {

    public final StatefulProcessor statefulProcessor;
    public final TaskWithTransfersEstimate taskEstimate;

    public ProcessorWithTaskEstimate(StatefulProcessor statefulProcessor, TaskWithTransfersEstimate taskEstimate) {
        this.statefulProcessor = statefulProcessor;
        this.taskEstimate = taskEstimate;
    }

    public String getProcessorId() {
        return statefulProcessor.getId();
    }

    public int getStartTime() {
        return taskEstimate.start;
    }

}
