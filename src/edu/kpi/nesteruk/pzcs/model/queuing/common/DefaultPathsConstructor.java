package edu.kpi.nesteruk.pzcs.model.queuing.common;

import edu.kpi.nesteruk.pzcs.model.common.GraphModelBundle;
import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;
import edu.kpi.nesteruk.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Anatolii on 2016-03-31.
 */
public class DefaultPathsConstructor<N extends Node, L extends Link<N>> {

    private final Map<String, N> nodesMap;
    private final Map<String, L> linksMap;

    public DefaultPathsConstructor(Map<String, N> nodesMap, Map<String, L> linksMap) {
        this.nodesMap = nodesMap;
        this.linksMap = linksMap;
    }

    public DefaultPathsConstructor(GraphModelBundle<N, L> graphBundle) {
        this(graphBundle.getNodesMap(), graphBundle.getLinksMap());
    }

    public Collection<List<N>> getAllPaths() {
        return GetVerticesWithoutIncomingEdges.getFirstTopologicalLevel(nodesMap, linksMap).stream()
                .map(this::getAllPathsFrom)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private Collection<List<N>> getAllPathsFrom(N node) {
        return expandPaths(Collections.singleton(Collections.singletonList(node)));
    }

    private int counter = 0;

    private Collection<List<N>> expandPaths(Collection<List<N>> paths) {
        Collection<List<N>> upgradedPaths = new ArrayList<>((paths.size()*3)/2);
        boolean allNotExpanded = true;
        for (List<N> path : paths) {
            N endOfPath = CollectionUtils.getLastOrNull(path);
            Collection<N> nextNodes = getNextNodes(endOfPath);
            if (CollectionUtils.isEmpty(nextNodes)) {
                upgradedPaths.add(path);
//                upgradedPaths.add(Collections.singletonList(endOfPath));
            } else {
                for (N nextNode : nextNodes) {
                    List<N> upgradedPath = new ArrayList<>(path);
                    upgradedPath.add(nextNode);
                    upgradedPaths.add(upgradedPath);
                    allNotExpanded = false;
                }
            }
        }
        if(counter >= 128) {
            System.out.println("Counter = " + counter);
        }
        counter++;

        if(allNotExpanded) {
            return paths;
        } else {
            return expandPaths(upgradedPaths);
        }
    }

    public Collection<N> getNextNodes(N node) {
        return getLinksFrom(node).stream()
                //Get destination of each node
                .map(Link::getSecond)
                .map(nodesMap::get)
                .collect(Collectors.toList());
    }

    public Collection<L> getLinksFrom(N node) {
        return linksMap.entrySet().stream()
                //Get only links from specified node
                .filter(entry -> entry.getValue().getFirst().equals(node.getId()))
                //Get link from map entry
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }
}
