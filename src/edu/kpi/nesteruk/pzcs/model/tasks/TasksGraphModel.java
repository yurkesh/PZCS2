package edu.kpi.nesteruk.pzcs.model.tasks;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.graph.generation.GraphGenerator;
import edu.kpi.nesteruk.pzcs.graph.validation.NonAcyclicGraphValidator;
import edu.kpi.nesteruk.pzcs.model.common.AbstractGraphModel;
import edu.kpi.nesteruk.pzcs.model.common.GraphModel;
import edu.kpi.nesteruk.pzcs.model.primitives.DirectedLink;
import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by Anatolii on 3/10/2016.
 */
public class TasksGraphModel extends AbstractGraphModel<Task, DirectedLink<Task>> implements GraphModel {

    public TasksGraphModel() {
        super(TasksGraphModel::makeGraph, true, new NonAcyclicGraphValidator());
    }

    private static Graph<String, String> makeGraph() {
        return new SimpleDirectedWeightedGraph<>(TasksGraphModel::getLinkId);
    }

    @Override
    protected Task makeConcreteNode(String nodeId, int weight) {
        return makeNode(nodeId, weight);
    }

    private static Task makeNode(String nodeId, int weight) {
        return new Task(nodeId, weight);
    }

    @Override
    protected Pair<DirectedLink<Task>, String> makeConcreteLink(Task source, Task destination, int weight) {
        return makeLink(source, destination, weight);
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

    public static GraphGenerator<Task, DirectedLink<Task>> makeGenerator(Random random) {
        return new GraphGenerator<>(random, TasksGraphModel::makeNode, TasksGraphModel::makeLink, TasksGraphModel::makeGraph);
    }
}
