package edu.kpi.nesteruk.pzcs.planning.state;

import edu.kpi.nesteruk.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by Yurii on 2016-05-25.
 */
public class ProcessorExecution {

    private final String processorId;
    private final int power;

    private List<String> execution = new ArrayList<>();

    public ProcessorExecution(String processorId, int power) {
        this.processorId = processorId;
        this.power = power;
    }

    public int size() {
        return execution.size();
    }

    public boolean isFree(int tact) {
        //It is free on specified tact if execution queue has (tact - 1) elements or there is no job for this tact
        return execution.size() == tact || execution.get(tact) == null;
    }

    public int getReleaseTime() {
        int size = size();
        if(size == 0) {
            return 0;
        }
        if(execution.get(size - 1) == null) {
            throw new IllegalStateException("Incorrect state of execution = " + execution);
        }
        return size;
    }

    public boolean hasTask(String task) {
        return execution.contains(task);
    }

    public boolean hasAllTasks(Collection<String> tasks) {
        if(CollectionUtils.isEmpty(tasks)) {
            throw new IllegalArgumentException("Tasks are empty = " + tasks);
        }
        return execution.containsAll(tasks);
    }

    public void addIdleExecution(int tact) {
        if(size() != tact) {
            throw new IllegalStateException("Incorrect state of execution = " + execution + ". Tact = " + tact);
        }
        execution.add(tact, null);
    }

    public void assignTask(int tact, String task, int weight) {
        if(execution.size() < tact) {
            //If number of elements in execution is less than tact -> need to fill execution with empty tacts
            for (int i = 0; i < tact - execution.size() - 1; i++) {
                execution.add(null);
            }
        }
        for (int i = 0; i < weight; i++) {
            execution.add(task);
        }
    }

    public Optional<String> getDoneTask(int tact) {
        if(execution.size() < tact) {
            throw new IllegalArgumentException("Processor = " + processorId + ". Tact = " + tact + ". Execution = " + execution);
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
    }
}
