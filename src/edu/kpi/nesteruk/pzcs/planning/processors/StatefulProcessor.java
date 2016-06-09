package edu.kpi.nesteruk.pzcs.planning.processors;

import edu.kpi.nesteruk.pzcs.model.system.Processor;
import edu.kpi.nesteruk.pzcs.planning.params.ProcessorsParams;
import edu.kpi.nesteruk.pzcs.planning.planner.ProcessorTransfer;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by Anatolii Bed on 2016-05-22.
 */
public class StatefulProcessor implements ScheduledJobsHolder {

    private final Processor processor;
    private final ProcessorsParams processorsParams;

    private final ProcessorExecution execution;
    private final ProcessorTransfers transfers;

    public StatefulProcessor(Processor processor, ProcessorsParams processorsParams) {
        this(
                processor,
                processorsParams,
                new ProcessorExecution(processor.getId(), processor.getWeight()),
                new ProcessorTransfers(processor.getId(), ProcessorsParams.numberOfChannelsNullSafe(processorsParams))
        );
    }

    private StatefulProcessor(Processor processor, ProcessorsParams processorsParams, ProcessorExecution execution, ProcessorTransfers transfers) {
        this.processor = processor;
        this.processorsParams = processorsParams;
        this.execution = execution;
        this.transfers = transfers;
    }

    public StatefulProcessor copy() {
        return new StatefulProcessor(
                processor,
                processorsParams,
                execution.copy(),
                transfers.copy()
        );
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

    public int getIdleTime(int tact) {
        return execution.getIdleTime(tact);
    }

    public void assignTask(int tact, String task, int weight) {
        execution.assignTask(tact, task, weight);
    }

    public void assignTransfer(ProcessorTransfer processorTransfer) {
        ChannelTransfer transfer = processorTransfer.channelTransfer;
        boolean isReceiver = processorTransfer.destProcessor.equals(processor.getId());
        int channel = isReceiver ? transfer.destChannel : transfer.srcChannel;
        String anotherProcessor;
        if(USE_EXTENDED_TRANSFER_FORMATTING) {
            anotherProcessor = isReceiver ? "<" + processorTransfer.srcProcessor : ">" + processorTransfer.destProcessor;
        } else {
            anotherProcessor = transfer.receiver;
        }
        transfers.addTransfer(
                channel,
                transfer.startTact,
                transfer.weight,
                anotherProcessor,
//                transfer.receiver,
                transfer.transfer
        );
    }

    @Override
    public String getExecutingTask(int tact) {
        return execution.getTask(tact);
    }

    @Override
    public int getNumberOfChannels() {
        return transfers.getNumberOfChannels();
    }

    @Override
    public String getTransfer(int channel, int tact) {
        return transfers.getTransfer(channel, tact);
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
