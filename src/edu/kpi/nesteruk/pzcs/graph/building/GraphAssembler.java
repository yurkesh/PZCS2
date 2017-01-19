package edu.kpi.nesteruk.pzcs.graph.building;

import edu.kpi.nesteruk.pzcs.graph.generation.GraphGenerator;
import edu.kpi.nesteruk.pzcs.model.common.GraphModelBundle;
import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;
import org.jgrapht.Graph;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2017-01-19.
 */
public class GraphAssembler<N extends Node, L extends Link<N>, G extends GraphModelBundle<N, L>> {

    public final GraphGenerator.NodeFactory<N> nodeFactory;
    public final GraphGenerator.LinkFactory<N, L> linkFactory;
    public final Supplier<? extends Graph<String, String>> graphFactory;
    public final BiFunction<Map<String, N>, Map<String, L>, G> graphBundleFactory;

    public GraphAssembler(
            GraphGenerator.NodeFactory<N> nodeFactory,
            GraphGenerator.LinkFactory<N, L> linkFactory,
            Supplier<? extends Graph<String, String>> graphFactory,
            BiFunction<Map<String, N>, Map<String, L>, G> graphBundleFactory) {

        this.nodeFactory = nodeFactory;
        this.linkFactory = linkFactory;
        this.graphFactory = graphFactory;
        this.graphBundleFactory = graphBundleFactory;
    }

    public G assemble(Collection<N> nodes, Map<String, L> links) {
        return graphBundleFactory.apply(
                nodes.stream().collect(Collectors.toMap(Node::getId, Function.identity())),
                links
        );
    }
}
