package edu.kpi.nesteruk.pzcs.model.common;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.model.primitives.IdAndValue;

import java.util.Collection;

/**
 * Created by Yurii on 2016-03-13.
 */
public interface GraphModel {

    NodeBuilder getNodeBuilder();

    LinkBuilder getLinkBuilder();

    void deleteNode(String id);

    void deleteLink(String id);

    boolean validate();

    /*
    String getSerialized();
    */

    GraphModelSerializable getSerializable();

    /**
     *
     * @param serializable
     * @return {nodes:[idAndValue], links:[{{sourceId, destinationId}, link:idAndValue}]}
     */
    Pair<Collection<IdAndValue>, Collection<Pair<Pair<String, String>, IdAndValue>>> restore(GraphModelSerializable serializable);
}
