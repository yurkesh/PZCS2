package edu.kpi.nesteruk.pzcs.planning.planner;

import edu.kpi.nesteruk.pzcs.graph.misc.GraphUtils;
import edu.kpi.nesteruk.pzcs.model.system.Processor;
import edu.kpi.nesteruk.pzcs.model.system.ProcessorsGraph;
import edu.kpi.nesteruk.pzcs.model.system.ProcessorsGraphBundle;
import edu.kpi.nesteruk.pzcs.model.tasks.Task;
import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraph;
import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraphBundle;
import edu.kpi.nesteruk.pzcs.planning.*;
import edu.kpi.nesteruk.pzcs.planning.params.ProcessorsParams;
import edu.kpi.nesteruk.pzcs.planning.processors.StatefulProcessor;
import edu.kpi.nesteruk.util.CollectionUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by Anatolii Bed on 2016-05-15.
 */
public class CommonPlanner implements Planner {

    private final ProcessorsGraphSimplifier processorsGraphSimplifier;
    private final ProcessorsToScheduleOnSorter processorsSorter;
    private final TasksGraphSimplifier tasksGraphSimplifier;
    private final TasksToScheduleSorter tasksSorter;
    private final SingleTaskHostSearcher singleTaskPlanner;
    private final Consumer<Object> logger;

    public CommonPlanner(
            ProcessorsGraphSimplifier processorsGraphSimplifier,
            ProcessorsToScheduleOnSorter processorsSorter,
            TasksGraphSimplifier tasksGraphSimplifier,
            TasksToScheduleSorter tasksSorter,
            SingleTaskHostSearcher singleTaskPlanner,
            Consumer<Object> logger) {

        this.processorsGraphSimplifier = processorsGraphSimplifier;
        this.processorsSorter = processorsSorter;
        this.tasksGraphSimplifier = tasksGraphSimplifier;
        this.tasksSorter = tasksSorter;
        this.singleTaskPlanner = Objects.requireNonNull(singleTaskPlanner);
        this.logger = logger;
    }


    @Override
    public SchedulingResult getPlannedWork(
            ProcessorsGraphBundle processorsGraphBundle,
            TasksGraphBundle tasksGraphBundle,
            ProcessorsParams params) {

        Map<String, Processor> allProcessors = processorsGraphBundle.getNodesMap();

        ProcessorsGraph processorsGraph = processorsGraphSimplifier.convertToGraph(processorsGraphBundle);

        //Sort processors by coherence
        List<String> processorsSorted = processorsSorter.sort(processorsGraph);
        logger.accept("Sorted processors:\n" + processorsSorted);


        params = params.numberOfChannels == ProcessorsParams.NUMBER_OF_CHANNELS_BY_MAX_COHERENCE ? new ProcessorsParams(
                GraphUtils.getMaxCoherence(processorsGraph)
        ) : params;
        Map<String, StatefulProcessor> statefulProcessorMap = makeStatefulProcessors(allProcessors.values(), params);

        Map<String, Task> tasksMap = tasksGraphBundle.getNodesMap();

        //Get tasks queue
        List<String> tasksSorted = tasksSorter.sort(tasksGraphBundle);
        logger.accept("Sorted tasks:\n" + tasksSorted);

        Set<String> doneTasks = new LinkedHashSet<>();
        DoneTasksHolder doneTasksHolder = DoneTasksHolder.getDoneTasksHolder(
                Collections.unmodifiableCollection(
                        tasksMap.values().stream().map(Task::getId).collect(Collectors.toList())
                ),
                doneTasks
        );

        TasksGraph tasksGraph = tasksGraphSimplifier.convertToGraph(tasksGraphBundle);

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
            return planner.getPlannedWork();
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
