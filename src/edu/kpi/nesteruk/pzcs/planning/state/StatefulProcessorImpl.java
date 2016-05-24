package edu.kpi.nesteruk.pzcs.planning.state;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.model.system.Processor;
import edu.kpi.nesteruk.pzcs.planning.params.ProcessorsParams;
import edu.kpi.nesteruk.pzcs.planning.transfering.Parcel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by Yurii on 2016-05-22.
 */
public class StatefulProcessorImpl implements StatefulProcessor {

    private final Processor processor;
    private final ProcessorsParams processorsParams;

    /*
    private List<String> execution = new ArrayList<>();
    */
    private final ProcessorExecution execution;
    private List<List<String>> transfers = new ArrayList<>();

    public StatefulProcessorImpl(Processor processor, ProcessorsParams processorsParams) {
        this.processor = processor;
        this.processorsParams = processorsParams;
        this.execution = new ProcessorExecution(processor.getId(), processor.getWeight());
    }

    @Override
    public StatefulProcessor copy() {
        return new StatefulProcessorImpl(processor, processorsParams);
    }

    @Override
    public String getProcessorId() {
        return processor.getId();
    }

    @Override
    public boolean isFree(int tact) {
        /*
        return execution.size() == tact || execution.get(tact) == null;
        */
        return execution.isFree(tact);
    }

    @Override
    public int getReleaseTime() {
        /*
        return execution.size();
        */
        return execution.getReleaseTime();
    }

    @Override
    public boolean hasTask(String task) {
        /*
        return execution.contains(task);
        */
        return execution.hasTask(task);
    }

    @Override
    public boolean hasAllTasks(Collection<String> tasks) {
        /*
        if(CollectionUtils.isEmpty(tasks)) {
            throw new IllegalArgumentException("Tasks are empty = " + tasks);
        }
        return execution.containsAll(tasks);
        */
        return execution.hasAllTasks(tasks);
    }

    @Override
    public void addIdleExecution(int tact) {
        /*
        execution.add(tact, null);
        */
        execution.addIdleExecution(tact);
    }

    @Override
    public void addIdleTransfer(int tact) {
        transfers.add(tact, new ArrayList<>());
    }

    @Override
    public List<Pair<String, Parcel>> getScheduledWork() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int sendTo(int startTact, int weight, StatefulProcessor receiver) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void assignTask(int tact, String task, int weight) {
        /*
        if(execution.size() < tact) {
            //If number of elements in execution is less than tact -> need to fill execution with empty tacts
            for (int i = 0; i < tact - execution.size() - 1; i++) {
                execution.add(null);
            }
        }
        for (int i = 0; i < weight; i++) {
            execution.add(task);
        }
        */
        execution.assignTask(tact, task, weight);
    }

    @Override
    public Optional<String> getDoneTask(int tact) {
        /*
        if(execution.size() < tact) {
            throw new IllegalArgumentException("Processor = " + processor.getId() + ". Tact = " + tact + ". Execution = " + execution);
        }
        if(execution.isEmpty()) {
            return Optional.empty();
        }

        //Task from previous tact
        String task = execution.get(tact - 1);

        if(task == null) {
            //Processor was free on previous tact
            return Optional.empty();
        }

        if(execution.size() == tact) {
            //If task is the last part of execution -> task from previous tact is done
            return Optional.of(task);
        } else {
            String currentTactTask = execution.get(tact);
            if(currentTactTask == null) {
                //If processor has no assigned tasks on this tact -> task from previous tact is done
                return Optional.of(task);
            } else {
                if(task.equals(currentTactTask)) {
                    //Processor is still working on task from previous tact
                    return Optional.empty();
                } else {
                    //Processor is working on new task -> task from previous tact is done
                    return Optional.of(task);
                }
            }
        }
        */
        return execution.getDoneTask(tact);
    }

    @Override
    public String toString() {
        return "StatefulProcessorImpl{" +
                "processor=" + processor +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StatefulProcessorImpl that = (StatefulProcessorImpl) o;

        return processor.equals(that.processor);

    }

    @Override
    public int hashCode() {
        return processor.hashCode();
    }
}
