package edu.kpi.nesteruk.pzcs.planning.planner;

import edu.kpi.nesteruk.pzcs.model.system.Processor;
import edu.kpi.nesteruk.pzcs.model.system.ProcessorsGraph;
import edu.kpi.nesteruk.pzcs.model.system.ProcessorsGraphBundle;
import edu.kpi.nesteruk.pzcs.model.tasks.Task;
import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraph;
import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraphBundle;
import edu.kpi.nesteruk.pzcs.planning.Planner;
import edu.kpi.nesteruk.pzcs.planning.params.ProcessorsParams;
import edu.kpi.nesteruk.pzcs.planning.processors.StatefulProcessor;
import edu.kpi.nesteruk.util.CollectionUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-05-15.
 */
public class CommonPlanner implements Planner {

    private final Function<ProcessorsGraphBundle, ProcessorsGraph> processorsGraphSimplifier;
    private final Function<ProcessorsGraph, List<String>> processorsByCoherenceDescSorter;
    private final Function<TasksGraphBundle, TasksGraph> tasksGraphSimplifier;
    private final Function<TasksGraphBundle, List<String>> tasksSorter;
    private final SingleTaskHostSearcher singleTaskPlanner;
    private final Consumer<Object> logger;

    public CommonPlanner(
            Function<ProcessorsGraphBundle, ProcessorsGraph> processorsGraphSimplifier,
            Function<ProcessorsGraph, List<String>> processorsByCoherenceDescSorter,
            Function<TasksGraphBundle, TasksGraph> tasksGraphSimplifier,
            Function<TasksGraphBundle, List<String>> tasksSorter,
            SingleTaskHostSearcher singleTaskPlanner,
            Consumer<Object> logger) {

        this.processorsGraphSimplifier = processorsGraphSimplifier;
        this.processorsByCoherenceDescSorter = processorsByCoherenceDescSorter;
        this.tasksGraphSimplifier = tasksGraphSimplifier;
        this.tasksSorter = tasksSorter;
        this.singleTaskPlanner = singleTaskPlanner;
        this.logger = logger;
    }


    @Override
    public Collection<StatefulProcessor> getPlannedWork(
            ProcessorsGraphBundle processorsGraphBundle,
            TasksGraphBundle tasksGraphBundle,
            ProcessorsParams params) {

        Map<String, Processor> allProcessors = processorsGraphBundle.getNodesMap();

        ProcessorsGraph processorsGraph = processorsGraphSimplifier.apply(processorsGraphBundle);

        //Sort processors by coherence
        List<String> processorsSorted = processorsByCoherenceDescSorter.apply(processorsGraph);
        logger.accept("Sorted processors:\n" + processorsSorted);

        Map<String, StatefulProcessor> statefulProcessorMap = makeStatefulProcessors(allProcessors.values(), params);

        Map<String, Task> tasksMap = tasksGraphBundle.getNodesMap();

        //Get tasks queue
        List<String> tasksSorted = tasksSorter.apply(tasksGraphBundle);
        logger.accept("Sorted tasks:\n" + tasksSorted);

        Set<String> doneTasks = new LinkedHashSet<>();
        DoneTasksHolder doneTasksHolder = DoneTasksHolder.getDoneTasksHolder(
                Collections.unmodifiableCollection(
                        tasksMap.values().stream().map(Task::getId).collect(Collectors.toList())
                ),
                doneTasks
        );

        TasksGraph tasksGraph = tasksGraphSimplifier.apply(tasksGraphBundle);

        DirectPredecessorsProvider taskPredecessorsProvider =
                DirectPredecessorsProvider.getDirectPredecessorsProvider(tasksGraph);

        ReadyTasksSupplier readyTasksSupplier = ReadyTasksSupplier.getReadyTasksSupplier(
                tasksGraph, doneTasks, taskPredecessorsProvider
        );

        TaskWithPredecessorsProvider taskWithPredecessorsMapper =
                TaskWithPredecessorsProvider.getTaskWithPredecessorsMapper(tasksGraph, taskPredecessorsProvider);

        ProcessorExecutingTaskProvider taskToExecutingProcessorMapper = ProcessorExecutingTaskProvider.getTaskToExecutingProcessorMapper(statefulProcessorMap.values());

        TaskWithHostedPredecessorsProvider taskWithHostedPredecessorsProvider =
                TaskWithHostedPredecessorsProvider.getTaskWithHostedPredecessorsProvider(
                        tasksGraphBundle, taskToExecutingProcessorMapper, allProcessors
                );

        TaskTransferRouter router = TaskTransferRouter.getRouter(
                processorsGraph,
                processorsGraphBundle
        );

        TasksGraphPlanner planner = new TasksGraphPlanner(
                logger,
                singleTaskPlanner,
                processorsSorted,
                statefulProcessorMap,
                tasksMap,
                tasksSorted,
                doneTasksHolder,
                taskPredecessorsProvider,
                readyTasksSupplier,
                taskWithPredecessorsMapper,
                taskToExecutingProcessorMapper,
                taskWithHostedPredecessorsProvider,
                router
        );

        try {
            // TODO: 2016-05-24 [REFACTOR] Make it look better
            planner.getPlannedWorkTime();
            return statefulProcessorMap.values();
        } catch (Exception e) {
            System.out.println("Processors:\n" + statefulProcessorMap.values().stream().map(Object::toString).collect(Collectors.joining("\n")));
            e.printStackTrace();
            return null;
        }
    }

    private static Map<String, StatefulProcessor> makeStatefulProcessors(Collection<Processor> processors, ProcessorsParams params) {
        return processors.stream()
                .collect(CollectionUtils.CustomCollectors.toMap(
                        Processor::getId,
                        processor -> new StatefulProcessor(processor, params),
                        LinkedHashMap::new
                ));
    }

}
