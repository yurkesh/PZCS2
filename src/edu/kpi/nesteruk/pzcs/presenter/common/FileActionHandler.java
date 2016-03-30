package edu.kpi.nesteruk.pzcs.presenter.common;

import java.awt.event.ActionEvent;

/**
 * Created by Anatolii on 2016-03-24.
 */
public interface FileActionHandler {
    void onNew(ActionEvent event);

    void onOpen(ActionEvent event);

    void onSave(ActionEvent event);
}
