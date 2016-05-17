package edu.kpi.nesteruk.pzcs.planning.tasks;

import edu.kpi.nesteruk.pzcs.model.primitives.DirectedLink;
import edu.kpi.nesteruk.pzcs.model.system.Processor;
import edu.kpi.nesteruk.pzcs.model.tasks.Task;

/**
 * Created by Yurii on 2016-05-18.
 */
public class TaskSourceImpl implements TaskDependencySource {

    public final DirectedLink<Task> transferBetweenTasks;
    public final Processor sourceTaskHost;

    public TaskSourceImpl(DirectedLink<Task> transferBetweenTasks, Processor sourceTaskHost) {
        this.transferBetweenTasks = transferBetweenTasks;
        this.sourceTaskHost = sourceTaskHost;
    }

    @Override
    public String getSourceTaskId() {
        return transferBetweenTasks.getFirst();
    }

    @Override
    public String getTargetTaskId() {
        return transferBetweenTasks.getSecond();
    }

    @Override
    public String getProcessorId() {
        return sourceTaskHost.getId();
    }

    /**
     * @return weight of transfer without assuming link between processors
     */
    @Override
    public int getTransferWeight() {
        return transferBetweenTasks.getWeight();
    }

}
