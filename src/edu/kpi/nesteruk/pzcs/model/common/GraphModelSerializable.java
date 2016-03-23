package edu.kpi.nesteruk.pzcs.model.common;

import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Created by Yurii on 2016-03-23.
 */
public class GraphModelSerializable<N extends Node, L extends Link<N>> implements Serializable {
    private Map<String, N> nodesMap;
    private Map<String, L> linksMap;

    public GraphModelSerializable() {
    }

    public GraphModelSerializable(Map<String, N> nodesMap, Map<String, L> linksMap) {
        this.nodesMap = new LinkedHashMap<>(nodesMap);
        this.linksMap = new LinkedHashMap<>(linksMap);
    }

    public LinkedHashSet<String> getNodesIds() {
        return new LinkedHashSet<>(nodesMap.keySet());
    }

    @Override
    public String toString() {
        return "GraphModelSerializable{" +
                "nodesMap=" + nodesMap +
                ", linksMap=" + linksMap +
                '}';
    }
}
