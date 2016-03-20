package edu.kpi.nesteruk.pzcs.presenter;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

/**
 * Created by Yurii on 2016-03-13.
 */
public interface GraphPresenter {
    String PROP_MX_CELL_EDGE = "edge";

    void onNewGraphEditor(ActionEvent event);

    void onNewGraphGenerator(ActionEvent event);

    void onOpenGraph(ActionEvent event);

    void onSaveGraph(ActionEvent event);

    void onNewSystem(ActionEvent event);

    void onOpenSystem(ActionEvent event);

    void onSaveSystem(ActionEvent event);

    void onProcessorsParams(ActionEvent event);

    void onGantDiagram(ActionEvent event);

    void onStatistics(ActionEvent event);

    void onAbout(ActionEvent event);

    void onExit(ActionEvent event);

    void onDoubleClick(MouseEvent event);

    void onContextMenuCall(MouseEvent event);

    void onValidate(ActionEvent event);
}
