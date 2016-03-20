package edu.kpi.nesteruk.pzcs.model.system;

import edu.kpi.nesteruk.misc.IdPool;
import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.graph.io.GraphSerializer;
import edu.kpi.nesteruk.pzcs.graph.validation.CompositeGraphValidator;
import edu.kpi.nesteruk.pzcs.graph.validation.GraphValidator;
import edu.kpi.nesteruk.pzcs.graph.validation.NoIsolatedEdgesGraphValidator;
import edu.kpi.nesteruk.pzcs.model.common.*;
import edu.kpi.nesteruk.pzcs.model.primitives.CongenericLink;
import edu.kpi.nesteruk.pzcs.model.primitives.IdAndValue;
import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-03-13.
 */
public class SystemGraphModel extends AbstractGraphModel<Processor, CongenericLink<Processor>> implements GraphModel {

    public SystemGraphModel() {
        super(
                SystemGraphModel::newGraph,
                true,
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

    @Override
    protected GraphSerializer<Processor, CongenericLink<Processor>> getGraphSerializer() {
        return null;
    }

    /*

    private IdPool<String> idPool = new CommonIdPool();

    private Map<String, Processor> processorsMap = new LinkedHashMap<>();

    private Map<String, CongenericLink<Processor>> linksMap = new LinkedHashMap<>();

    private final Graph<String, String> graph = new SimpleDirectedWeightedGraph<>(this::getLinkId);

    @Override
    public NodeBuilder getNodeBuilder() {
        return new CommonNodeBuilder(
                false,
                idPool::obtainID,
                id -> {
                    id = id.toLowerCase();
                    boolean unique = !processorsMap.containsKey(id);
                    if(unique) {
                        idPool.obtainId(id);
                    } else {
                        idPool.releaseId(id);
                    }
                    return unique;
                },
                this::makeProcessor
        );
    }

    public IdAndValue makeProcessor(String id, int _weight) {
        Processor processor = new Processor(id);
        processorsMap.put(id, processor);
        graph.addVertex(id);
        return new IdAndValue(id, processor.toString());
    }

    @Override
    public LinkBuilder getLinkBuilder() {
        return new CommonLinkBuilder(true, this::canConnect, this::connect);
    }

    public boolean canConnect(String firstId, String secondId) {
        return true;
    }

    public IdAndValue connect(String firstId, String secondId, int weight) {
        CongenericLink<Processor> link = new CongenericLink<>(processorsMap.get(firstId), processorsMap.get(secondId), weight);
        String linkId = getLinkId(link);
        graph.addEdge(firstId, secondId, linkId);
        return linksMap.putIfAbsent(linkId, link) == null ? new IdAndValue(linkId, link.toString()) : null;
    }

    private String getLinkId(CongenericLink<Processor> processorLink) {
        return getLinkId(processorLink.getFirst().getId(), processorLink.getSecond().getId());
    }

    private String getLinkId(String srcId, String destId) {
        List<String> idsList = new ArrayList<>();
        idsList.add(srcId);
        idsList.add(destId);
        Collections.sort(idsList);
        return idsList.stream().collect(Collectors.joining("=-="));
    }

    @Override
    public void deleteNode(String id) {
        processorsMap.computeIfPresent(id, (processorId, processor) -> {
            idPool.releaseId(processor.id);
            graph.removeVertex(id);
            return null;
        });
    }

    @Override
    public void deleteLink(String id) {
        linksMap.remove(id);
        graph.removeEdge(id);
    }

    @Override
    public boolean validate() {
        return new CompositeGraphValidator(
                new NoIsolatedEdgesGraphValidator()
        ).isValid(new GraphDataAdapter<>(processorsMap, linksMap, this::getLinkId).getGraphData());
    }

    */
}
