package edu.kpi.nesteruk.pzcs.model.common;

import edu.kpi.nesteruk.pzcs.common.GraphDataAssembly;
import edu.kpi.nesteruk.pzcs.graph.generation.GraphGenerator;

/**
 * Created by Yurii on 2016-03-13.
 */
public interface GraphModel {

    NodeBuilder getNodeBuilder();

    LinkBuilder getLinkBuilder();

    void deleteNode(String id);

    void deleteLink(String id);

    boolean validate();

    GraphModelBundle getSerializable();

    GraphDataAssembly generate(GraphGenerator.Params params);

    GraphDataAssembly restore(GraphModelBundle serializable);

    void reset();
}
