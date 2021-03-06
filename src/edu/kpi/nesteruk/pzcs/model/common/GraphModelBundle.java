package edu.kpi.nesteruk.pzcs.model.common;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.model.primitives.CongenericLink;
import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Created by Yurii on 2016-03-23.
 */
public class GraphModelBundle<N extends Node, L extends Link<N>> implements Serializable {

    private static final long serialVersionUID = 6624076855744169362L;

    private Map<String, N> nodesMap;
    private Map<String, L> linksMap;
//    private transient Map<Pair<String, String>, String> nodesToLink;

    public GraphModelBundle(Map<String, N> nodesMap, Map<String, L> linksMap) {
        this.nodesMap = new LinkedHashMap<>(nodesMap);
        this.linksMap = new LinkedHashMap<>(linksMap);
    }

    public LinkedHashSet<String> getNodesIds() {
        return new LinkedHashSet<>(nodesMap.keySet());
    }

    public Map<String, N> getNodesMap() {
        return new LinkedHashMap<>(nodesMap);
    }

    public Map<String, L> getLinksMap() {
        return new LinkedHashMap<>(linksMap);
    }

    public String getLinkBetweenNodes(String firstNode, String secondNode) {
        return linksMap.entrySet().stream()
                .filter(linkEntry -> {
                    L link = linkEntry.getValue();
                    return (link.getFirst().equals(firstNode) && link.getSecond().equals(secondNode)) ||
                            (link.getFirst().equals(secondNode) && link.getSecond().equals(firstNode));
                })
                .map(Map.Entry::getKey)
                .findFirst()
                .get();
    }

    @Override
    public String toString() {
        return "GraphModelBundle{" +
                "nodesMap=" + nodesMap +
                ", linksMap=" + linksMap +
                '}';
    }
}
