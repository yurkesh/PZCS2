package edu.kpi.nesteruk.pzcs.planning.tasks;

import edu.kpi.nesteruk.pzcs.model.primitives.DirectedLink;
import edu.kpi.nesteruk.pzcs.model.system.Processor;
import edu.kpi.nesteruk.pzcs.model.tasks.Task;

/**
 * Contains info about dependency of concrete task (source)
 * Created by Yurii on 2016-05-18.
 */
public class TaskHostedDependency {

    public final DirectedLink<Task> transferBetweenTasks;
    public final Processor sourceTaskHost;

    public TaskHostedDependency(DirectedLink<Task> transferBetweenTasks, Processor sourceTaskHost) {
        this.transferBetweenTasks = transferBetweenTasks;
        this.sourceTaskHost = sourceTaskHost;
    }

    /**
     *
     * @return id of source (predecessor) task
     */
    public String getSourceTaskId() {
        return transferBetweenTasks.getFirst();
    }

    /**
     * @return id of destination (dependent/successor) task
     */
    public String getDestinationTaskId() {
        return transferBetweenTasks.getSecond();
    }

    /**
     * @return id of processor holding source tasl
     */
    public String getProcessorId() {
        return sourceTaskHost.getId();
    }

    public String getTransferId() {
        return transferBetweenTasks.getFirst() + "-" + transferBetweenTasks.getSecond();
    }

    /**
     * @return weight of transfer (volume of data) that needs to be transferred from source to destination
     */
    public int getTransferWeight() {
        return transferBetweenTasks.getWeight();
    }

    @Override
    public String toString() {
        return "TaskHostedDependency{" +
                "transferBetweenTasks=" + transferBetweenTasks +
                ", sourceTaskHost=" + sourceTaskHost +
                '}';
    }
}
