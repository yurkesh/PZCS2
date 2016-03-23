package edu.kpi.nesteruk.pzcs.presenter.common;

import java.awt.event.ActionEvent;

/**
 * Created by Yurii on 2016-03-23.
 */
public interface SystemPresenter extends GraphPresenter {
    void onNewSystem(ActionEvent event);

    void onOpenSystem(ActionEvent event);

    void onSaveSystem(ActionEvent event);

    void onProcessorsParams(ActionEvent event);
}
