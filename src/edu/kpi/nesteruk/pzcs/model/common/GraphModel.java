package edu.kpi.nesteruk.pzcs.model.common;

/**
 * Created by Yurii on 2016-03-13.
 */
public interface GraphModel {

    NodeBuilder getNodeBuilder();

    LinkBuilder getLinkBuilder();

    void deleteNode(String id);

    void deleteLink(String id);

    boolean validate();
}
