package edu.kpi.nesteruk.pzcs.planning;

import edu.kpi.nesteruk.misc.FunctionWithCache;
import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.model.primitives.CongenericLink;
import edu.kpi.nesteruk.pzcs.model.primitives.DirectedLink;
import edu.kpi.nesteruk.pzcs.model.system.Processor;
import edu.kpi.nesteruk.pzcs.model.system.ProcessorsGraph;
import edu.kpi.nesteruk.pzcs.model.system.ProcessorsGraphBundle;
import edu.kpi.nesteruk.pzcs.model.tasks.Task;
import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraph;
import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraphBundle;
import edu.kpi.nesteruk.pzcs.planning.params.PlanningParams;
import edu.kpi.nesteruk.pzcs.planning.state.StatefulProcessor;
import edu.kpi.nesteruk.pzcs.planning.tasks.TaskSourceImpl;
import edu.kpi.nesteruk.pzcs.planning.tasks.TaskWithHostedPredecessors;
import edu.kpi.nesteruk.pzcs.planning.tasks.TaskWithPredecessors;
import edu.kpi.nesteruk.pzcs.planning.transfering.Parcel;
import edu.kpi.nesteruk.util.CollectionUtils;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-05-15.
 */
abstract class CommonPlanner implements Planner {

    private Function<ProcessorsGraphBundle, ProcessorsGraph> processorsGraphSimplifier;
    private Function<ProcessorsGraphBundle, List<String>> processorsByCoherenceSorter;
    private Function<TasksGraphBundle, List<String>> tasksSorter;
    private Function<TasksGraphBundle, TasksGraph> tasksGraphSimplifier;

    private int maxTaskTactsDelay;

//    private final QueueConstructor<Task, DirectedLink<Task>> tasksQueueConstructor;
//    private TaskReadinessPredicate taskReadinessPredicate;


    @Override
    public Map<Processor, List<Pair<Task, Parcel>>> getPlannedWork(
            ProcessorsGraphBundle processorsGraphBundle,
            TasksGraphBundle tasksGraphBundle,
            PlanningParams params) {

        Map<String, Processor> allProcessors = processorsGraphBundle.getNodesMap();
        Map<String, Task> allTasks = tasksGraphBundle.getNodesMap();

        //Sort processors by coherence
        List<String> processorsSorted = processorsByCoherenceSorter.apply(processorsGraphBundle);
        //Get tasks queue
        List<String> tasksSorted = tasksSorter.apply(tasksGraphBundle);
        final Set<String> doneTasks = new LinkedHashSet<>();

        Map<String, StatefulProcessor> processors = makeStatefulProcessors(allProcessors.values());
        TasksGraph tasksGraph = tasksGraphSimplifier.apply(tasksGraphBundle);
        Function<String, Collection<String>> taskPredecessorsProvider = TaskWithPredecessors.getDirectPredecessorsProvider(tasksGraph);
        Supplier<Collection<String>> readyTasksSupplier = getReadyTasksSupplier(
                tasksGraph, doneTasks, taskPredecessorsProvider
        );
        Function<Integer, Map<String, StatefulProcessor>> freeProcessorsProvider = getFreeProcessorsProvider(processors);

        Function<String, TaskWithPredecessors> taskWithPredecessorsMapper = getTaskWithPredecessorsMapper(tasksGraph, taskPredecessorsProvider);
        Function<String, String> taskToExecutingProcessorMapper = getTaskToExecutingProcessorMapper();

        BiFunction<Processor, Processor, List<CongenericLink<Processor>>> router = getRouter(processorsGraphSimplifier.apply(processorsGraphBundle), processorsGraphBundle);

        int tact = 0;
        do {
            //Get all ready tasks
            Collection<String> readyTasks = readyTasksSupplier.get();
            //Get all free processors
            Map<String, StatefulProcessor> freeProcessors = freeProcessorsProvider.apply(tact);

            tasksSorted.stream()
                    //Iterate over ready task keeping initial order
                    .filter(readyTasks::contains)
                    //Map task -> TaskWithPredecessors
                    .map(taskWithPredecessorsMapper)

                    //Need to search for processor which provides minimal start time
                    //Step-by-step:

                    .map(taskWithPredecessors -> {
                        //1) Get transfers IDs from all direct predecessors: {task -> link_between_tasks_id}
                        Map<String, String> transfersFromPredecessorsIds = taskWithPredecessors.getTransfersFromPredecessors();

                        //2) Get transfers from all direct predecessors: {task -> link_between_tasks}
                        Map<String, DirectedLink<Task>> transfersFromPredecessors = transfersFromPredecessorsIds.entrySet().stream()
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        entry -> tasksGraphBundle.getLinksMap().get(entry.getValue())
                                ));

                        //3) Get processors of predecessors: {task -> processor}
                        Map<String, String> tasksOnProcessors = transfersFromPredecessors.keySet().stream()
                                .collect(Collectors.toMap(
                                        Function.identity(),
                                        taskToExecutingProcessorMapper
                                ));

                        //4) Get sources of task - transfer from each processor without assuming links between processors
                        List<TaskSourceImpl> taskSources = transfersFromPredecessors.entrySet().stream()
                                .map(entry -> new TaskSourceImpl(
                                        entry.getValue(),
                                        allProcessors.get(tasksOnProcessors.get(entry.getKey()))
                                ))
                                .collect(Collectors.toList());

                        return new TaskWithHostedPredecessors(taskWithPredecessors.task, taskSources);
                    })
                    .forEach(taskWithHostedPredecessors -> {

                    });

            //repeat until all tasks are done
        } while (doneTasks.containsAll(allTasks.keySet()));

        return null;
    }

    /**
     * @return function that accepts source and destination processors and returns list of links between processors that
     * provide minimal total weight
     */
    private static BiFunction<Processor, Processor, List<CongenericLink<Processor>>> getRouter(
            ProcessorsGraph processorsGraph,
            ProcessorsGraphBundle processorsGraphBundle) {
        return null;
    }

    private static Function<String, String> getTaskToExecutingProcessorMapper() {
        return task -> null;
    }

    private static Function<String, TaskWithPredecessors> getTaskWithPredecessorsMapper(
            TasksGraph tasksGraph,
            Function<String, Collection<String>> tasksPredecessorsProvider) {

        return task -> new TaskWithPredecessors(
                task,
                tasksPredecessorsProvider,
                //Use cache
                new FunctionWithCache<>(predecessor -> tasksGraph.getEdge(predecessor, task))
        );
    }

    private static Function<Integer, Map<String, StatefulProcessor>> getFreeProcessorsProvider(Map<String, StatefulProcessor> processors) {
        return tact -> processors.entrySet().stream()
                .filter(processorEntry -> processorEntry.getValue().isFree(tact))
                .collect(CollectionUtils.CustomCollectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        LinkedHashMap::new
                ));
    }

    private static Map<String, StatefulProcessor> makeStatefulProcessors(Collection<Processor> processors) {
        return processors.stream()
                .collect(CollectionUtils.CustomCollectors.toMap(
                        Processor::getId,
                        null,
                        LinkedHashMap::new
                ));
    }

    private static Supplier<Collection<String>> getReadyTasksSupplier(
            TasksGraph tasksGraph,
            Collection<String> doneTasks,
            Function<String, Collection<String>> predecessorsProvider) {

        return () -> tasksGraph.vertexSet().stream()
                .map(vertex -> {
                    //Get predecessors (parents)
                    Collection<String> predecessors = new LinkedHashSet<>(predecessorsProvider.apply(vertex));
                    //Remove all executed tasks from predecessors
                    predecessors.removeAll(doneTasks);
                    //{task, non_executed_predecessors}
                    return Pair.create(vertex, (Set<String>) predecessors);
                })
                //Accept tasks without non-executed predecessors
                .filter(pair -> pair.second.isEmpty())
                //Get task from pair
                .map(Pair::getFirst)
                .collect(Collectors.toList());
    }

    public static List<String> sortByCoherence(Graph<String, String> graph) {
        return graph.vertexSet().stream()
                //Map to pair {vertex, neighbours_of_vertex}
                .map(vertex -> Pair.create(vertex, Graphs.neighborListOf(graph, vertex)))
                //Sort by number of neighbors
                .sorted(Comparator.comparing(pair -> pair.second.size()))
                //Get vertex
                .map(Pair::getFirst)
                .collect(Collectors.toList());
    }

}
