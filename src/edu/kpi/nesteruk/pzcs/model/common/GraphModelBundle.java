package edu.kpi.nesteruk.pzcs.model.common;

import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Created by Anatolii on 2016-03-23.
 */
public class GraphModelBundle<N extends Node, L extends Link<N>> implements Serializable {

    private static final long serialVersionUID = 6624076855744169362L;

    private Map<String, N> nodesMap;
    private Map<String, L> linksMap;

    public GraphModelBundle(Map<String, N> nodesMap, Map<String, L> linksMap) {
        this.nodesMap = new LinkedHashMap<>(nodesMap);
        this.linksMap = new LinkedHashMap<>(linksMap);
    }

    public LinkedHashSet<String> getNodesIds() {
        return new LinkedHashSet<>(nodesMap.keySet());
    }

    Map<String, N> getNodesMap() {
        return nodesMap;
    }

    Map<String, L> getLinksMap() {
        return linksMap;
    }

    @Override
    public String toString() {
        return "GraphModelBundle{" +
                "nodesMap=" + nodesMap +
                ", linksMap=" + linksMap +
                '}';
    }
}
