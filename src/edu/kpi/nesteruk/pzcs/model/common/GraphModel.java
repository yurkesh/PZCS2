package edu.kpi.nesteruk.pzcs.model.common;

import edu.kpi.nesteruk.pzcs.common.GraphDataAssembly;
import edu.kpi.nesteruk.pzcs.model.primitives.IdAndValue;

import java.util.Collection;

/**
 * Created by Yurii on 2016-03-13.
 */
public interface GraphModel {

    NodeBuilder getNodeBuilder();

    LinkBuilder getLinkBuilder();

    /**
     * @param id
     * @return collection of IDs of incident edges
     */
    Collection<String> deleteNode(String id);

    void deleteLink(String id);

    boolean validate();

    GraphModelBundle getBundle();

    GraphDataAssembly restore(GraphModelBundle modelBundle);

    void reset();

    /**
     * @param idOfLink
     * @param text
     * @return new String value to set in corresponding cell
     */
    IdAndValue updateWeightOfLink(String idOfLink, String text);

    /**
     * @param idOfNode
     * @param text
     * @return new String value to set in corresponding cell
     */
    IdAndValue updateWeightOfNode(String idOfNode, String text);
}
