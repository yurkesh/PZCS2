package edu.kpi.nesteruk.pzcs.planning.planner;

import java.util.List;

/**
 * Created by Anatolii Bed on 2016-05-26.
 */
class TaskTransfer {

    public final String srcTask;
    public final String destTask;

    public final List<ProcessorTransfer> processorTransfers;

    public TaskTransfer(String srcTask, String destTask, List<ProcessorTransfer> processorTransfers) {
        this.srcTask = srcTask;
        this.destTask = destTask;
        this.processorTransfers = processorTransfers;
    }
}
