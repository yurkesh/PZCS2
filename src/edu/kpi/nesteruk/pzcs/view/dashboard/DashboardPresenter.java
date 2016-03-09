package edu.kpi.nesteruk.pzcs.view.dashboard;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.view.mxEdgeStyle;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import edu.kpi.nesteruk.log.Log;
import edu.kpi.nesteruk.misc.InterchangeableTuple;
import edu.kpi.nesteruk.pzcs.model.DashboardModel;
import edu.kpi.nesteruk.pzcs.model.Task;
import edu.kpi.nesteruk.pzcs.view.Views;
import edu.kpi.nesteruk.pzcs.view.dialog.Dialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.*;

/**
 * Created by Yurii on 2016-02-17.
 */
public class DashboardPresenter {

    private static final String TAG = "DashboardPresenter";

    public static final String PROP_MX_CELL_EDGE = "edge";

    private final DashboardView dashboardView;

    private final mxGraph graph;
    private final mxGraphComponent graphComponent;
    private final Object parent;

    private final DashboardModel dashboardModel;

    private final Map<String, mxICell> cellsMap = new LinkedHashMap<>();

    public DashboardPresenter(DashboardView dashboardView) {
        this.dashboardView = dashboardView;

        this.graph = new mxGraph() {

            @Override
            public boolean isCellFoldable(Object cell, boolean collapse) {
                return false;
            }

            @Override
            public boolean isCellDisconnectable(Object cell, Object terminal, boolean source) {
                return !((mxICell) cell).isEdge();
            }
        };
        parent = graph.getDefaultParent();

        applyGraphSettings();
        initGraphListeners();

        this.graphComponent = new mxGraphComponent(graph);

        dashboardModel = new DashboardModel();

        dashboardView.setGraphComponent(graphComponent);
    }

    private void initGraphListeners() {
        graph.addListener(mxEvent.CELL_CONNECTED, (sender, evt) -> {
            Map<String, Object> props = evt.getProperties();
            mxCell edge = (mxCell) props.get(PROP_MX_CELL_EDGE);
            mxICell sourceCell = edge.getSource();
            mxICell targetCell = edge.getTarget();

            if (sourceCell != null && targetCell != null) {
                Task source = (Task) sourceCell.getValue();
                Task target = (Task) targetCell.getValue();

                String sourceId = source.getId();
                String targetId = target.getId();

                boolean connected = false;

                if(dashboardModel.canConnect(sourceId, targetId)) {
                    Dialog.InputWeightDialog inputWeightDialog =
                            new Dialog.InputWeightDialog("Connecting tasks", "Set connection weight:");
                    Integer weight = inputWeightDialog.show();
                    if(weight != null) {
                        edge.setValue(new InterchangeableTuple<>(sourceId, targetId));
                        connected = true;
                    }
                }

                if(!connected) {
                    graph.getModel().remove(edge);
                }
            }

        });
    }

    private void applyGraphSettings() {
        mxStylesheet stylesheet = new mxStylesheet();
        stylesheet.setDefaultEdgeStyle(getEdgeStyles());
        stylesheet.setDefaultVertexStyle(getVertexStyles());
        graph.setStylesheet(stylesheet);
    }

    private Map<String, Object> getVertexStyles() {
        Map<String, Object> vertexStyle = graph.getStylesheet().getDefaultVertexStyle();
        vertexStyle.put(mxConstants.STYLE_FOLDABLE, 0);
        return vertexStyle;
    }

    private Map<String, Object> getEdgeStyles() {
        Map<String, Object> edgeStyle = graph.getStylesheet().getDefaultEdgeStyle();
        edgeStyle.put(mxConstants.STYLE_EDGE, mxEdgeStyle.ElbowConnector);
        edgeStyle.put(mxConstants.STYLE_SHAPE,    mxConstants.SHAPE_CONNECTOR);
        edgeStyle.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
        edgeStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000");
        edgeStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
        edgeStyle.put(mxConstants.STYLE_LABEL_BACKGROUNDCOLOR, "#f0f0f0");

        return edgeStyle;
    }

    public void onNewGraphEditor(ActionEvent event) {

    }

    public void onNewGraphGenerator(ActionEvent event) {

    }

    public void onOpenGraph(ActionEvent event) {

    }

    public void onSaveGraph(ActionEvent event) {

    }

    public void onNewSystem(ActionEvent event) {

    }

    public void onOpenSystem(ActionEvent event) {

    }

    public void onSaveSystem(ActionEvent event) {

    }

    public void onProcessorsParams(ActionEvent event) {

    }

    public void onGantDiagram(ActionEvent event) {

    }

    public void onStatistics(ActionEvent event) {

    }

    public void onAbout(ActionEvent event) {

    }

    public void onExit(ActionEvent event) {
        System.exit(0);
    }

    public void onDoubleClick(MouseEvent event) {
        addNode(event.getX(), event.getY());
    }

    mxICell addNode(int x, int y) {
        mxICell cell;
        graph.getModel().beginUpdate();
        try {
            Task task = dashboardModel.newTask();
            cell = insertVertex(x, y, task);

            cellsMap.put(task.getId(), cell);
        } finally {
            graph.getModel().endUpdate();
        }
        return cell;
    }

    private mxICell insertVertex(int x, int y, Task task) {
        return (mxICell) graph.insertVertex(parent, task.getId(), task, x, y, Views.Tasks.TASK_DIAMETER, Views.Tasks.TASK_DIAMETER, Views.Tasks.TASK_STYLE);
    }

    boolean deleteNode(int x, int y) {
        return false;
    }

    public Collection<JMenuItem> getMenuItemsForClick(MouseEvent event) {
        final int x = event.getX();
        final int y = event.getY();
        return Arrays.asList(
                new JMenuItem(new AbstractAction("Add node") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        addNode(x, y);
                    }
                }),
                new JMenuItem(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        deleteNode(x, y);
                    }
                })
        );
    }
}
