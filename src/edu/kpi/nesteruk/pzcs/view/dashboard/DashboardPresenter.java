package edu.kpi.nesteruk.pzcs.view.dashboard;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import edu.kpi.nesteruk.log.Log;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by Yurii on 2016-02-17.
 */
public class DashboardPresenter {

    private static final String TAG = "DashboardPresenter";

    private final DashboardView dashboardView;

    private final mxGraph graph;
    private final mxGraphComponent graphComponent;

    public DashboardPresenter(DashboardView dashboardView) {
        this.dashboardView = dashboardView;
        this.graph = new mxGraph();
        this.graphComponent = new mxGraphComponent(graph);
        dashboardView.setGraphComponent(graphComponent);

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
        Log.d(TAG, "onDoubleClick");
    }

    mxCell addNode(int x, int y) {
        mxCell cell;
        graph.getModel().beginUpdate();
        try {
            cell = insertVertex(node, x, y, node.nodeType.style);
            node.cell = cell;
            addPorts(cell, node);
            nodes.add(node);
            nodesToCells.put(node, cell);
        } finally {
            graph.getModel().endUpdate();
        }
        return cell;
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
