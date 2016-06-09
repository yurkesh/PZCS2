package edu.kpi.nesteruk.pzcs.model.tasks;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.common.GraphDataAssembly;
import edu.kpi.nesteruk.pzcs.graph.generation.GeneratorParams;
import edu.kpi.nesteruk.pzcs.graph.generation.GraphGenerator;
import edu.kpi.nesteruk.pzcs.graph.validation.NonAcyclicGraphValidator;
import edu.kpi.nesteruk.pzcs.model.common.AbstractGraphModel;
import edu.kpi.nesteruk.pzcs.model.common.GraphModel;
import edu.kpi.nesteruk.pzcs.model.primitives.DirectedLink;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 3/10/2016.
 */
public class TasksGraphModel extends AbstractGraphModel<Task, DirectedLink<Task>, TasksGraphBundle> implements GraphModel {

    public TasksGraphModel() {
        super(TasksGraphModel::makeGraph, true, new NonAcyclicGraphValidator());
    }

    public static TasksGraph makeGraph() {
        return new TasksGraph(TasksGraphModel::getLinkId);
    }

    private static Task makeNode(String nodeId, int weight) {
        return new Task(nodeId, weight);
    }

    @Override
    protected Task makeConcreteNode(String nodeId, int weight) {
        return makeNode(nodeId, weight);
    }

    @Override
    protected Pair<DirectedLink<Task>, String> makeConcreteLink(Task source, Task destination, int weight) {
        return makeLink(source, destination, weight);
    }

    @Override
    protected TasksGraphBundle makeBundle(Map<String, Task> nodesMap, Map<String, DirectedLink<Task>> linksMap) {
        return new TasksGraphBundle(nodesMap, linksMap);
    }

    private static Pair<DirectedLink<Task>, String> makeLink(Task source, Task destination, int weight) {
        DirectedLink<Task> link = new DirectedLink<>(source, destination, weight);
        return Pair.create(link, getLinkId(source.getId(), destination.getId()));
    }

    private static String getLinkId(String srcId, String destId) {
        List<String> idsList = new ArrayList<>();
        idsList.add(srcId);
        idsList.add(destId);
        Collections.sort(idsList);
        return idsList.stream().collect(Collectors.joining("=-="));
    }

    public GraphDataAssembly generate(GeneratorParams generatorParams) {
        return apply(new GraphGenerator<>(
                this::makeConcreteNode,
                this::makeConcreteLink,
                TasksGraphModel::makeGraph,
                this::makeBundle
        ).generate(generatorParams));
    }

    public static GraphGenerator<Task, DirectedLink<Task>, TasksGraphBundle> makeGenerator(Random random) {
        return new GraphGenerator<>(
                random,
                TasksGraphModel::makeNode,
                TasksGraphModel::makeLink,
                TasksGraphModel::makeGraph,
                TasksGraphBundle::new
        );
    }
}
