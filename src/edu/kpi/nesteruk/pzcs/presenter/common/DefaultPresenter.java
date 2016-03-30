package edu.kpi.nesteruk.pzcs.presenter.common;

import java.awt.event.ActionEvent;

/**
 * Created by Anatolii on 2016-03-23.
 */
public interface DefaultPresenter {
    void onGantDiagram(ActionEvent event);

    void onStatistics(ActionEvent event);

    void onAbout(ActionEvent event);

    default void onExit(ActionEvent event) {
        System.exit(0);
    }
}
