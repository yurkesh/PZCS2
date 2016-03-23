package edu.kpi.nesteruk.pzcs.presenter.common;

import java.awt.event.ActionEvent;

/**
 * Created by Yurii on 2016-03-23.
 */
public interface TasksPresenter extends GraphPresenter {
    void onNewGraphEditor(ActionEvent event);

    void onNewGraphGenerator(ActionEvent event);

    void onOpenGraph(ActionEvent event);

    void onSaveGraph(ActionEvent event);
}
