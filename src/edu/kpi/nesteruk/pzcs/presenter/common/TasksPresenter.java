package edu.kpi.nesteruk.pzcs.presenter.common;

import java.awt.event.ActionEvent;

/**
 * Created by Yurii on 2016-03-23.
 */
public interface TasksPresenter extends GraphPresenter {
    default void onNewGraphEditor(ActionEvent event) {
        onNew(event);
    }

    void onNewGraphGenerator(ActionEvent event);

    default void onOpenGraph(ActionEvent event) {
        onOpen(event);
    }

    default void onSaveGraph(ActionEvent event) {
        onSave(event);
    }
}
