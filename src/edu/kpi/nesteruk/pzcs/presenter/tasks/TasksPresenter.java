package edu.kpi.nesteruk.pzcs.presenter.tasks;

import edu.kpi.nesteruk.pzcs.presenter.common.GraphPresenter;

import java.awt.event.ActionEvent;

/**
 * Created by Anatolii on 2016-03-23.
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

    void onMakeQueues(ActionEvent event);
}
