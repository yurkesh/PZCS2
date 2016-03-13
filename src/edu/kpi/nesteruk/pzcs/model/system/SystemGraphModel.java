package edu.kpi.nesteruk.pzcs.model.system;

import edu.kpi.nesteruk.misc.IdPool;
import edu.kpi.nesteruk.pzcs.model.common.*;
import edu.kpi.nesteruk.pzcs.model.common.primitive.CongenericLink;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Yurii on 2016-03-13.
 */
public class SystemGraphModel implements GraphModel {

    private IdPool idPool = new IdPool();

    private Map<String, Processor> processorsMap = new LinkedHashMap<>();

    private Set<CongenericLink<Processor>> edges = new LinkedHashSet<>();

    @Override
    public NodeBuilder getNodeBuilder() {
        return new CommonNodeBuilder(
                false,
                idPool::obtainID,
                id -> {
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

    public String makeProcessor(String id, int _weight) {
        Processor processor = new Processor(id);
        processorsMap.put(id, processor);
        return processor.toString();
    }

    @Override
    public LinkBuilder getLinkBuilder() {
        return new CommonLinkBuilder(true, this::canConnect, this::connect);
    }

    public boolean canConnect(String firstId, String secondId) {
        return true;
    }

    public String connect(String firstId, String secondId, int weight) {
        CongenericLink<Processor> link = new CongenericLink<>(processorsMap.get(firstId), processorsMap.get(secondId), weight);
        boolean added = edges.add(link);
        if(added) {
            return link.toString();
        } else {
            return null;
        }
    }

    @Override
    public void deleteNode(String id) {
        processorsMap.computeIfPresent(id, (processorId, processor) -> {
            idPool.releaseId(processor.id);
            return null;
        });
    }

    @Override
    public boolean validate() {
        return true;
    }
}
