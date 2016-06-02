package edu.kpi.nesteruk.pzcs.planning.planner;

import edu.kpi.nesteruk.util.CollectionUtils;

import java.util.Collection;

/**
 * Created by Anatolii Bed on 2016-05-26.
 */
public class TaskWithTransfersEstimate {

    public final int start;
    public final boolean transfersNeeded;
    public final Collection<TaskTransfer> taskTransfers;

    public TaskWithTransfersEstimate(int start, Collection<TaskTransfer> taskTransfers) {
        this.start = start;
        this.taskTransfers = taskTransfers;
        transfersNeeded = !CollectionUtils.isEmpty(taskTransfers);
    }
}
