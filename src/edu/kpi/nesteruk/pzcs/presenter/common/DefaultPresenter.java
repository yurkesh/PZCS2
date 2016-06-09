package edu.kpi.nesteruk.pzcs.presenter.common;

import java.awt.event.ActionEvent;

/**
 * Created by Yurii on 2016-03-23.
 */
public interface DefaultPresenter {

    int ACTION_EVENT_OPEN_FILE = 1;

    void onGantDiagram(ActionEvent event);

    void onAbout(ActionEvent event);

    default void onExit(ActionEvent event) {
        System.exit(0);
    }
}
