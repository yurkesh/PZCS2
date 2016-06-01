package edu.kpi.nesteruk.pzcs.planning.planner;

import edu.kpi.nesteruk.pzcs.model.tasks.Task;
import edu.kpi.nesteruk.pzcs.planning.processors.ProcessorWithTaskEstimate;
import edu.kpi.nesteruk.pzcs.planning.processors.StatefulProcessor;
import edu.kpi.nesteruk.pzcs.planning.tasks.TaskWithHostedDependencies;
import edu.kpi.nesteruk.pzcs.view.print.Table;
import edu.kpi.nesteruk.pzcs.view.print.TableRepresentationBuilder;
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
    private final SingleTaskHostSearcher singleTaskPlanner;
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
            SingleTaskHostSearcher singleTaskPlanner,
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

    public int getPlannedWorkTime() {

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
            doneTasksHolder.addDoneTasks(tact, doneTasks);

            if(!doneTasksHolder.hasNotDone()) {
                break;
            }

            //Get all ready tasks
            Collection<String> readyTasks = readyTasksSupplier.getIdOfReadyTasks();
            readyTasks.removeAll(executingTasks);

            Map<TaskWithHostedDependencies, ProcessorWithTaskEstimate> map = tasksSorted.stream()
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
                                ProcessorWithTaskEstimate processorWithTaskEstimate = processorsSorted.stream()
                                        //processorId -> StatefulProcessor
                                        .map(statefulProcessorMap::get)
                                        //StatefulProcessor -> {StatefulProcessor, startTime}
                                        .map(statefulProcessor -> new ProcessorWithTaskEstimate(
                                                statefulProcessor,
                                                singleTaskPlanner.getStartTime(
                                                        router,
                                                        tact,
                                                        taskWithHostedPredecessors,
                                                        statefulProcessor,
                                                        doneTasksHolder,
                                                        processorID -> statefulProcessorMap.get(processorID).copy()
                                                )
                                        ))
                                        //Get {StatefulProcessor, startTime} with the lowest (best) startTime (sort by
                                        // startTime, asc)
                                        .min(Comparator.comparing(ProcessorWithTaskEstimate::getStartTime))
                                        .get();

                                String taskId = taskWithHostedPredecessors.task;
                                //Need to schedule task on found processor to change state of processors system
                                // before scheduling next task
                                processorWithTaskEstimate.statefulProcessor.assignTask(
                                        processorWithTaskEstimate.getStartTime(),
                                        taskId,
                                        tasksMap.get(taskId).getWeight()
                                );
                                //Also need to schedule all transfers
                                if(processorWithTaskEstimate.taskEstimate.transfersNeeded) {
                                    applyTransfers(processorWithTaskEstimate.taskEstimate.taskTransfers);
                                }
                                return processorWithTaskEstimate;
                            },
                            LinkedHashMap::new
                    ));

            //Get ids of tasks scheduled in this tact
            List<String> scheduledTasks = map.keySet().stream()
                    .map(TaskWithHostedDependencies::getId)
                    .collect(Collectors.toList());

            //Add scheduled tasks to executing
            executingTasks.addAll(scheduledTasks);

            logger.accept("[" + tact + "]\nPlaced tasks\n" + map.entrySet().stream()
                    .sorted(
                            Comparator.<Map.Entry<TaskWithHostedDependencies, ProcessorWithTaskEstimate>, String>comparing(
                                    entry -> entry.getValue().getProcessorId()
                            ).reversed()
                    )
                    .map(entry -> entry.getKey().task + " -> " + entry.getValue().getProcessorId())
                    .collect(Collectors.joining("\n"))
            );

            tactCounter.incrementAndGet();



            //repeat until all tasks are done
        } while (doneTasksHolder.hasNotDone());

        {
            Table executionTable = makeExecutionTable(tactCounter.get());
            String executionTableStr = new TableRepresentationBuilder(executionTable).getRepresentation();
            System.out.println("\n\n" + executionTableStr);
        }

        {
            Table transfersTable = makeTransfersTable(tactCounter.get());
            String transfersTableStr = new TableRepresentationBuilder(transfersTable).getRepresentation();
            System.out.println("\n\n" + transfersTableStr);
        }

        return tactCounter.decrementAndGet();
    }

    private Table makeTransfersTable(int tacts) {
        int processors = statefulProcessorMap.size();
        int channels = statefulProcessorMap.get(String.valueOf("1")).getNumberOfChannels() + 1;

        return new Table() {
            @Override
            public String[] getColumnsNames() {
                String[] columns = new String[tacts + 1];
                columns[0] = "#";
                for (int i = 1; i <= tacts; i++) {
                    columns[i] = String.valueOf(i - 1);
                }
                return columns;
            }

            @Override
            public String[][] getColumnsData() {
                String[][] data = new String[processors * channels][];
                for (int processor = 0; processor < processors; processor++) {
                    String processorId = String.valueOf(processor + 1);
                    for (int channel = 0; channel < channels; channel++) {
                        int globalChannel = processor * channels + channel;
                        if(channel == channels - 1) {
                            String[] div = new String[tacts + 1];
                            Arrays.fill(div, TableRepresentationBuilder.DIV_ESCAPE + "-");
                            data[globalChannel] = div;
                            continue;
                        }
                        data[globalChannel] = new String[tacts + 1];
                        if(channel == 0) {
                            data[globalChannel][0] = processorId;
                        }

                        StatefulProcessor statefulProcessor = statefulProcessorMap.get(processorId);
                        String transfer;
                        for (int tact = 0; tact < tacts; tact++) {
                            transfer = statefulProcessor.getTransfer(channel, tact);
                            data[globalChannel][tact + 1] = transfer;
                        }
                    }
                }
                return data;
            }
        };
    }

    private Table makeExecutionTable(int tacts) {
        int numberOfProcessors = statefulProcessorMap.size();

        return new Table() {
            @Override
            public String[] getColumnsNames() {
                String[] columns = new String[tacts + 1];
                columns[0] = "#";
                for (int i = 1; i <= tacts; i++) {
                    columns[i] = String.valueOf(i - 1);
                }
                return columns;
            }

            @Override
            public String[][] getColumnsData() {
                String[][] data = new String[numberOfProcessors * 2][];
                String previousTask = null;
                for (int globalProcessor = 0; globalProcessor < numberOfProcessors * 2; globalProcessor++) {
                    if(globalProcessor % 2 == 0) {
                        String[] div = new String[tacts + 1];
                        Arrays.fill(div, TableRepresentationBuilder.DIV_ESCAPE + "-");
                        data[globalProcessor] = div;
                        continue;
                    }
                    int processor = globalProcessor / 2 + 1;
                    data[globalProcessor] = new String[tacts + 1];
                    String processorId = String.valueOf(processor);
                    data[globalProcessor][0] = processorId;

                    StatefulProcessor statefulProcessor = statefulProcessorMap.get(processorId);
                    String task;
                    for (int tact = 0; tact < tacts; tact++) {
                        task = statefulProcessor.getExecutingTask(tact);
                        String cell = task == null ? "" : (task.equals(previousTask) ? task : "*" + task);
                        previousTask = task;
                        data[globalProcessor][tact + 1] = cell;
                    }
                }
                return data;
            }
        };
    }

    private void applyTransfers(Collection<TaskTransfer> taskTransfers) {
        taskTransfers.stream()
                .forEach(taskTransfer -> taskTransfer.processorTransfers.stream()
                        .forEach(processorTransfer -> {
                            statefulProcessorMap.get(processorTransfer.srcProcessor).assignTransfer(
                                    processorTransfer
                            );
                            statefulProcessorMap.get(processorTransfer.destProcessor).assignTransfer(
                                    processorTransfer
                            );
                        })
                );
    }


}
