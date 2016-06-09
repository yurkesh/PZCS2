package edu.kpi.nesteruk.pzcs.presenter.system;

import edu.kpi.nesteruk.pzcs.model.system.ProcessorsGraphBundle;
import edu.kpi.nesteruk.pzcs.presenter.common.GraphPresenter;

import java.awt.event.ActionEvent;

/**
 * Created by Anatolii on 2016-03-23.
 */
public interface SystemPresenter extends GraphPresenter {
    default void onNewSystem(ActionEvent event) {
        onNew(event);
    }

    default void onOpenSystem(ActionEvent event) {
        onOpen(event);
    }

    default void onSaveSystem(ActionEvent event) {
        onSave(event);
    }

    void onStatistics(ActionEvent event);

    ProcessorsGraphBundle getProcessorsGraphBundle();
}
