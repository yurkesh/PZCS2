package edu.kpi.nesteruk.pzcs.model.system;

import edu.kpi.nesteruk.misc.IdPool;
import edu.kpi.nesteruk.pzcs.graph.validation.CompositeGraphValidator;
import edu.kpi.nesteruk.pzcs.graph.validation.NoIsolatedEdgesGraphValidator;
import edu.kpi.nesteruk.pzcs.model.common.*;
import edu.kpi.nesteruk.pzcs.model.primitives.CongenericLink;
import edu.kpi.nesteruk.pzcs.model.primitives.IdAndValue;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-03-13.
 */
public class SystemGraphModel implements GraphModel {

    private IdPool<String> idPool = new CommonIdPool();

    private Map<String, Processor> processorsMap = new LinkedHashMap<>();

    private Map<String, CongenericLink<Processor>> linksMap = new LinkedHashMap<>();

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
        return linksMap.putIfAbsent(linkId, link) == null ? new IdAndValue(linkId, link.toString()) : null;
    }

    private String getLinkId(CongenericLink<Processor> processorLink) {
        /*
        String firstId = processorLink.getFirst().getId();
        String secondId = processorLink.getSecond().getId();
        int comp = firstId.compareTo(secondId);
        if(comp == 0) {
            throw new IllegalStateException(processorLink.toString());
        } else if (comp < 0) {
            return String.format("%s>-<%s", (comp < 0 ? {firstId, secondId} : ))
        }
        */
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
            return null;
        });
    }

    @Override
    public void deleteLink(String id) {
        linksMap.remove(id);
    }

    @Override
    public boolean validate() {
        return new CompositeGraphValidator(
                new NoIsolatedEdgesGraphValidator()
        ).isValid(new GraphDataAdapter<>(processorsMap, linksMap, this::getLinkId).getGraphData());
    }
}
