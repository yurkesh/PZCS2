package edu.kpi.nesteruk.pzcs.planning.processors;

import edu.kpi.nesteruk.util.CollectionUtils;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Created by Yurii on 2016-05-25.
 */
class ProcessorExecution {

    private final String processorId;
    private final int power;

    private final Map<Integer, String> execution;

    public ProcessorExecution(String processorId, int power) {
        this(processorId, power, new TreeMap<>());
    }

    private ProcessorExecution(String processorId, int power, Map<Integer, String> execution) {
        this.processorId = processorId;
        this.power = power;
        this.execution = execution;
    }

    public ProcessorExecution copy() {
        Map<Integer, String> eCopy = new TreeMap<>();
        eCopy.putAll(execution);
        return new ProcessorExecution(processorId, power, eCopy);
    }

    public int size() {
        return execution.size();
    }

    public boolean isFree(int tact) {
        //It is free on specified tact if execution queue has (tact - 1) elements or there is no job for this tact
        return execution.size() == tact || execution.get(tact) == null;
    }
/*
    public int getReleaseTime(int tact) {
        execution.

        int size = size();
        if(size == 0) {
            return 0;
        }
        if(execution.get(size - 1) == null) {
            throw new IllegalStateException("Incorrect state of execution = " + execution);
        }
        return size;
    }*/

    public boolean hasTask(String task) {
        return execution.values().contains(task);
    }

    public boolean hasAllTasks(Collection<String> tasks) {
        if(CollectionUtils.isEmpty(tasks)) {
            throw new IllegalArgumentException("Tasks are empty = " + tasks);
        }
        return execution.values().containsAll(tasks);
    }

    public int getMinStartTime(int startTact, int weight) {
        for(;; startTact++) {
            if(areAllNextTactsFree(startTact, weight)) {
                return startTact;
            }
        }
    }

    /**
     * @return true if execution queue has no tasks at tacts [startTact, weight)
     */
    private boolean areAllNextTactsFree(int startTact, int weight) {
        return !IntStream.range(startTact, startTact + weight)
                .filter(execution::containsKey)
                .findAny()
                .isPresent();
    }

    public void assignTask(int tact, String task, int weight) {
        IntStream.range(tact, tact + weight)
                .forEach(futureTact -> execute(futureTact, task));
    }

    private void execute(int tact, String action) {
        String removed = execution.put(tact, action);
        if(removed != null) {
            throw new IllegalStateException(
                    "Incorrect state of execution = " + execution
                            + ". Processor = " + processorId
                            + ". Tact = " + tact
                            + ". Action = '" + action
                            + "'. Removed = '" + removed + "'"
            );
        }
    }

    /**
     *
     * @param tact to check
     * @return task that was executing at previous tact and becomes done at specified tact
     */
    public Optional<String> getDoneTask(int tact) {
        /*
        for (int tactCheck = tact; tactCheck >= 0; tactCheck--) {
            Optional<String> taskOpt = getDoneTaskInner(tactCheck);
            if(taskOpt.isPresent()) {
                return taskOpt;
            }
        }
        return Optional.empty();
    }

    public Optional<String> getDoneTaskInner(int tact) {
        */
        if(execution.isEmpty()) {
            return Optional.empty();
        }

        //Task from previous tact
        String task = execution.get(tact - 1);
        if(task == null) {
            //Processor was free on previous tact
            return Optional.empty();
        }

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

    public String getTask(int tact) {
        return execution.get(tact);
    }

    @Override
    public String toString() {
        return "" + execution;
    }

    /**
     *
     * @param tact
     * @return if (tact == 0) returns 0; else return number of previous tacts that processor was free
     */
    public int getIdleTime(int tact) {
        if(tact == 0) {
            return 0;
        }
        int idleTactsCounter = 0;
        while (--tact >= 0 && isFree(tact)) {
            idleTactsCounter++;
        }
        return idleTactsCounter;
    }
}
