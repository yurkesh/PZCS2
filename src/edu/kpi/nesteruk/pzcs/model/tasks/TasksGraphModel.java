package edu.kpi.nesteruk.pzcs.model.tasks;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.graph.validation.CompositeGraphValidator;
import edu.kpi.nesteruk.pzcs.graph.validation.NonAcyclicGraphValidator;
import edu.kpi.nesteruk.pzcs.graph.validation.NonSplitGraphValidator;
import edu.kpi.nesteruk.pzcs.model.common.AbstractGraphModel;
import edu.kpi.nesteruk.pzcs.model.common.GraphModel;
import edu.kpi.nesteruk.pzcs.model.primitives.DirectedLink;
import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 3/10/2016.
 */
public class TasksGraphModel extends AbstractGraphModel<Task, DirectedLink<Task>> implements GraphModel {

    public TasksGraphModel() {
        super(TasksGraphModel::newGraph, true, new NonAcyclicGraphValidator());
    }

    private static Graph<String, String> newGraph() {
        return new SimpleDirectedWeightedGraph<>(TasksGraphModel::getLinkId);
    }

    @Override
    protected Task makeConcreteNode(String nodeId, int weight) {
        return new Task(nodeId, weight);
    }

    @Override
    protected Pair<DirectedLink<Task>, String> makeConcreteLink(String srcId, String destId, int weight) {
        DirectedLink<Task> link = new DirectedLink<>(getNode(srcId), getNode(destId), weight);
        return Pair.create(link, link.toString());
    }

    private static String getLinkId(String srcId, String destId) {
        List<String> idsList = new ArrayList<>();
        idsList.add(srcId);
        idsList.add(destId);
        Collections.sort(idsList);
        return idsList.stream().collect(Collectors.joining("=-="));
    }
}
