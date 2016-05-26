package edu.kpi.nesteruk.pzcs.planning.processors;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.model.system.Processor;
import edu.kpi.nesteruk.pzcs.planning.params.ProcessorsParams;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by Yurii on 2016-05-22.
 */
public class StatefulProcessor {

    private final Processor processor;
    private final ProcessorsParams processorsParams;

    private final ProcessorExecution execution;
    private final ProcessorTransfers transfers;

    public StatefulProcessor(Processor processor, ProcessorsParams processorsParams) {
        this.processor = processor;
        this.processorsParams = processorsParams;
        String processorId = processor.getId();
        this.execution = new ProcessorExecution(processorId, processor.getWeight());
        this.transfers = new ProcessorTransfers(processorId, ProcessorsParams.numberOfChannelsNullSafe(processorsParams));
    }

    public StatefulProcessor copy() {
        return new StatefulProcessor(processor, processorsParams);
    }

    public String getId() {
        return processor.getId();
    }

    public boolean isFree(int tact) {
        return execution.isFree(tact);
    }

    public boolean hasTask(String task) {
        return execution.hasTask(task);
    }

    public boolean hasAllTasks(Collection<String> tasks) {
        return execution.hasAllTasks(tasks);
    }

    public ChannelTransfer getSendingEstimate(int startTact, int weight, StatefulProcessor receiver) {
        return transfers.getTransferEstimate(startTact, weight, receiver.transfers);
    }

    public int getMinStartTime(int startTact, int weight) {
        return execution.getMinStartTime(startTact, weight);
    }

    public void assignTask(int tact, String task, int weight) {
        execution.assignTask(tact, task, weight);
    }

    public void assignTransfer(ChannelTransfer transfer) {
        transfers.addTransfer(transfer.srcChannel, transfer.startTact, transfer.weight, transfer.receiver, transfer.transfer);
    }

    public Optional<String> getDoneTask(int tact) {
        return execution.getDoneTask(tact);
    }

    @Override
    public String toString() {
        return "P{" +
                "[" + processor.getId() + "]" +
                "\nexecution = " + execution +
                "\ntransfers = " + transfers +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StatefulProcessor that = (StatefulProcessor) o;

        return processor.equals(that.processor);

    }

    @Override
    public int hashCode() {
        return processor.hashCode();
    }
}
