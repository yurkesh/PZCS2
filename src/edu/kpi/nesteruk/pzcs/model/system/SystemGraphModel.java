package edu.kpi.nesteruk.pzcs.model.system;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.graph.validation.NoIsolatedEdgesGraphValidator;
import edu.kpi.nesteruk.pzcs.model.common.AbstractGraphModel;
import edu.kpi.nesteruk.pzcs.model.common.GraphModel;
import edu.kpi.nesteruk.pzcs.model.primitives.CongenericLink;
import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-03-13.
 */
public class SystemGraphModel extends AbstractGraphModel<Processor, CongenericLink<Processor>> implements GraphModel {

    public SystemGraphModel() {
        super(
                SystemGraphModel::newGraph,
                false,
                new NoIsolatedEdgesGraphValidator()
        );
    }

    private static Graph<String, String> newGraph() {
        return new SimpleWeightedGraph<>(SystemGraphModel::getLinkId);
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
        return new Processor(nodeId);
    }

    @Override
    protected Pair<CongenericLink<Processor>, String> makeConcreteLink(String srcId, String destId, int weight) {
        return Pair.create(new CongenericLink<>(getNode(srcId), getNode(destId), weight), getLinkId(srcId, destId));
    }
}
