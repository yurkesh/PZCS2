package edu.kpi.nesteruk.pzcs.planning.planner;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.model.system.Processor;
import edu.kpi.nesteruk.pzcs.model.tasks.Task;
import edu.kpi.nesteruk.pzcs.planning.state.ProcessorWithStartTime;
import edu.kpi.nesteruk.pzcs.planning.state.StatefulProcessor;
import edu.kpi.nesteruk.pzcs.planning.tasks.TaskWithHostedDependencies;
import edu.kpi.nesteruk.pzcs.planning.transfering.Parcel;
import edu.kpi.nesteruk.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-05-22.
 */
class TasksGraphPlanner {

    private final Consumer<Object> logger;
    private final SingleTaskScheduleSearcher singleTaskPlanner;
    private final List<String> processorsSorted;
    private final Map<String, StatefulProcessor> statefulProcessorMap;
    private final Map<String, Task> tasksMap;
    private final List<String> tasksSorted;
    private final DoneTasksHolder doneTasksHolder;
    private final DirectPredecessorsProvider taskPredecessorsProvider;
    private final ReadyTasksSupplier readyTasksSupplier;
    private final TaskWithPredecessorsProvider taskWithPredecessorsMapper;
    private final ProcessorExecutingTaskProvider taskToExecutingProcessorMapper;
    private final TaskWithHostedPredecessorsProvider taskWithHostedPredecessorsMapper;
    private final TaskTransferRouter router;

    TasksGraphPlanner(
            Consumer<Object> logger,
            SingleTaskScheduleSearcher singleTaskPlanner,
            List<String> processorsSorted,
            Map<String, StatefulProcessor> statefulProcessorMap,
            Map<String, Task> tasksMap,
            List<String> tasksSorted,
            DoneTasksHolder doneTasksHolder,
            DirectPredecessorsProvider taskPredecessorsProvider,
            ReadyTasksSupplier readyTasksSupplier,
            TaskWithPredecessorsProvider taskWithPredecessorsMapper,
            ProcessorExecutingTaskProvider taskToExecutingProcessorMapper,
            TaskWithHostedPredecessorsProvider taskWithHostedPredecessorsMapper,
            TaskTransferRouter router) {

        this.logger = logger;
        this.singleTaskPlanner = singleTaskPlanner;
        this.processorsSorted = processorsSorted;
        this.statefulProcessorMap = statefulProcessorMap;
        this.tasksMap = tasksMap;
        this.tasksSorted = tasksSorted;
        this.doneTasksHolder = doneTasksHolder;
        this.taskPredecessorsProvider = taskPredecessorsProvider;
        this.readyTasksSupplier = readyTasksSupplier;
        this.taskWithPredecessorsMapper = taskWithPredecessorsMapper;
        this.taskToExecutingProcessorMapper = taskToExecutingProcessorMapper;
        this.taskWithHostedPredecessorsMapper = taskWithHostedPredecessorsMapper;
        this.router = router;
    }

    public Map<Processor, List<Pair<Task, Parcel>>> getPlannedWork() {

        Set<String> executingTasks = new LinkedHashSet<>();

        AtomicInteger tactCounter = new AtomicInteger();
        do {

            final int tact = tactCounter.get();

            //Get all done tasks
            List<String> doneTasks = statefulProcessorMap.values().stream()
                    .map(statefulProcessor -> statefulProcessor.getDoneTask(tact))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());

            //Add them to holder
            doneTasksHolder.addDoneTasks(doneTasks);

            if(!doneTasksHolder.hasNotDone()) {
                break;
            }

            //Get all ready tasks
            Collection<String> readyTasks = readyTasksSupplier.getIdOfReadyTasks();
            readyTasks.removeAll(executingTasks);

            Map<TaskWithHostedDependencies, ProcessorWithStartTime> map = tasksSorted.stream()
                    //Iterate over all ready task keeping initial order
                    .filter(readyTasks::contains)
                    //taskId -> TaskWithPredecessors
                    .map(taskWithPredecessorsMapper::getTaskWithItsPredecessors)
                    //TaskWithPredecessors -> TaskWithHostedDependencies
                    .map(taskWithHostedPredecessorsMapper::getTaskWithHostedPredecessors)
                    .collect(CollectionUtils.CustomCollectors.toMap(
                            Function.<TaskWithHostedDependencies>identity(),
                            taskWithHostedPredecessors -> {
                                //Find processor with the best start time for this task
                                Optional<ProcessorWithStartTime> processorWithTheBestStartTimeOpt = processorsSorted.stream()
                                        //processorId -> StatefulProcessor
                                        .map(statefulProcessorMap::get)
                                        //StatefulProcessor -> {StatefulProcessor, startTime}
                                        .map(statefulProcessor -> new ProcessorWithStartTime(
                                                statefulProcessor,
                                                singleTaskPlanner.getStartTime(
                                                        router, tact, taskWithHostedPredecessors, statefulProcessor
                                                )
                                        ))
                                        //Sort by startTime, asc
                                        .sorted(Comparator.comparing(ProcessorWithStartTime::getStartTime))
                                        //Get {StatefulProcessor, startTime} with the lowest (best) startTime
                                        .findFirst();

                                if (processorWithTheBestStartTimeOpt.isPresent()) {
                                    ProcessorWithStartTime processorWithStartTime = processorWithTheBestStartTimeOpt.get();
                                    String taskId = taskWithHostedPredecessors.task;
                                    //Need to schedule task on found processor to change state of processors system
                                    // before scheduling next task
                                    processorWithStartTime.statefulProcessor.assignTask(
                                            tact, taskId, tasksMap.get(taskId).getWeight()
                                    );
                                    return processorWithStartTime;
                                } else {
                                    //What shall I do?
                                    throw new IllegalStateException("Cannot find processor for task = " + taskWithPredecessorsMapper);
                                }
                            },
                            LinkedHashMap::new
                    ));

            //Get ids of tasks scheduled in this tact
            List<String> scheduledTasks = map.keySet().stream()
                    .map(TaskWithHostedDependencies::getTaskId)
                    .collect(Collectors.toList());
            
            //Get all processors that received scheduled task on this tact
            List<StatefulProcessor> scheduledProcessors = map.values().stream()
                    .map(processorWithStartTime -> statefulProcessorMap.get(processorWithStartTime.getProcessorId()))
                    .collect(Collectors.toList());

            //For all processors that on this tact:
            // did not received scheduled task
            // and
            // are not busy
            // - mark this tact as idle
            statefulProcessorMap.values().stream()
                    .filter(statefulProcessor -> !scheduledProcessors.contains(statefulProcessor))
                    .filter(statefulProcessor -> statefulProcessor.isFree(tact))
                    .forEach(statefulProcessor -> statefulProcessor.addIdleExecution(tact));


            //Add scheduled tasks to executing
            executingTasks.addAll(scheduledTasks);

            logger.accept("[" + tact + "]\nPlaced tasks\n" + map.entrySet().stream()
                    .sorted(
                            Comparator.<Map.Entry<TaskWithHostedDependencies, ProcessorWithStartTime>, String>comparing(
                                    entry -> entry.getValue().getProcessorId()
                            ).reversed()
                    )
                    .map(entry -> entry.getKey().task + " -> " + entry.getValue().getProcessorId())
                    .collect(Collectors.joining("\n"))
            );

            tactCounter.incrementAndGet();

            if(tact > 10) {
                break;
            }

            //repeat until all tasks are done
        } while (doneTasksHolder.hasNotDone());

        return null;
    }


}
