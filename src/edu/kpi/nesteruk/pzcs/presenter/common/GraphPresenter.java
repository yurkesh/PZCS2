package edu.kpi.nesteruk.pzcs.presenter.common;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

/**
 * Created by Yurii on 2016-03-13.
 */
public interface GraphPresenter {

    String PROP_MX_CELL_EDGE = "edge";

    void onDoubleClick(MouseEvent event);

    void onContextMenuCall(MouseEvent event);

    void onValidate(ActionEvent event);

    void onNew(ActionEvent event);

    void onOpen(ActionEvent event);

    void onSave(ActionEvent event);

}
