package edu.kpi.nesteruk.pzcs.model.system;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.graph.validation.CompositeGraphValidator;
import edu.kpi.nesteruk.pzcs.graph.validation.NoIsolatedEdgesGraphValidator;
import edu.kpi.nesteruk.pzcs.graph.validation.NonSplitGraphValidator;
import edu.kpi.nesteruk.pzcs.model.common.AbstractGraphModel;
import edu.kpi.nesteruk.pzcs.model.common.GraphModel;
import edu.kpi.nesteruk.pzcs.model.common.GraphModelBundle;
import edu.kpi.nesteruk.pzcs.model.primitives.CongenericLink;
import org.jgrapht.Graph;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Anatolii on 2016-03-13.
 */
public class SystemGraphModel extends AbstractGraphModel<Processor, CongenericLink<Processor>, ProcessorsGraphBundle> implements GraphModel {

    public SystemGraphModel() {
        super(
                SystemGraphModel::newGraph,
                true,
                new CompositeGraphValidator<String, String>(new NoIsolatedEdgesGraphValidator(), new NonSplitGraphValidator())
        );
    }

    public static ProcessorsGraph newGraph() {
        return new ProcessorsGraph(SystemGraphModel::getLinkId);
    }

    private static String getLinkId(String srcId, String destId) {
        List<String> idsList = new ArrayList<>();
        idsList.add(srcId);
        idsList.add(destId);
        Collections.sort(idsList);
        return idsList.stream().collect(Collectors.joining("=-="));
    }

    @Override
    protected Processor makeConcreteNode(String nodeId, int weight) {
        return new Processor(nodeId, weight);
    }

    @Override
    protected Pair<CongenericLink<Processor>, String> makeConcreteLink(Processor source, Processor destination, int weight) {
        return Pair.create(new CongenericLink<>(source, destination, weight), getLinkId(source.getId(), destination.getId()));
    }

    @Override
    protected ProcessorsGraphBundle makeBundle(Map<String, Processor> nodesMap, Map<String, CongenericLink<Processor>> linksMap) {
        return new ProcessorsGraphBundle(nodesMap, linksMap);
    }
}
