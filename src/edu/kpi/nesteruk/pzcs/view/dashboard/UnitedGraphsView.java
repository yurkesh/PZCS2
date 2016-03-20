package edu.kpi.nesteruk.pzcs.view.dashboard;

import edu.kpi.nesteruk.pzcs.common.GraphType;
import edu.kpi.nesteruk.pzcs.view.common.CommonGraphView;
import edu.kpi.nesteruk.pzcs.presenter.GraphPresenter;
import edu.kpi.nesteruk.pzcs.view.common.GraphView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Yurii on 2016-03-13.
 */
public class UnitedGraphsView implements GraphPresenter {

    private Map<GraphType, GraphView> typedGraphViews = new LinkedHashMap<>();

    private GraphType currentGraph;

    private final JTabbedPane tabbedPane = new JTabbedPane();

    public UnitedGraphsView(GraphType... types) {
        initTabs(types);
        currentGraph = types[0];
    }

    private void initTabs(GraphType... graphTypes) {
        for (GraphType graphType : graphTypes) {
            CommonGraphView graphView = graphType.getGraphView();
            tabbedPane.addTab(graphType.getCaption(), graphView);
            typedGraphViews.put(graphType, graphView);
        }
    }

    public JComponent getGraphsContainer() {
        return tabbedPane;
    }

    public GraphPresenter getGraphPresenter() {
        return typedGraphViews.get(currentGraph).getPresenter();
    }

    @Override
    public void onNewGraphEditor(ActionEvent event) {
        getGraphPresenter().onNewGraphEditor(event);
    }

    @Override
    public void onNewGraphGenerator(ActionEvent event) {
        getGraphPresenter().onNewGraphGenerator(event);
    }

    @Override
    public void onOpenGraph(ActionEvent event) {
        getGraphPresenter().onOpenGraph(event);
    }

    @Override
    public void onSaveGraph(ActionEvent event) {
        getGraphPresenter().onSaveGraph(event);
    }

    @Override
    public void onNewSystem(ActionEvent event) {
        getGraphPresenter().onNewSystem(event);
    }

    @Override
    public void onOpenSystem(ActionEvent event) {
        getGraphPresenter().onOpenSystem(event);
    }

    @Override
    public void onSaveSystem(ActionEvent event) {
        getGraphPresenter().onSaveSystem(event);
    }

    @Override
    public void onProcessorsParams(ActionEvent event) {
        getGraphPresenter().onProcessorsParams(event);
    }

    @Override
    public void onGantDiagram(ActionEvent event) {
        getGraphPresenter().onGantDiagram(event);
    }

    @Override
    public void onStatistics(ActionEvent event) {
        getGraphPresenter().onStatistics(event);
    }

    @Override
    public void onAbout(ActionEvent event) {
        getGraphPresenter().onAbout(event);
    }

    @Override
    public void onExit(ActionEvent event) {
        getGraphPresenter().onExit(event);
    }

    @Override
    public void onDoubleClick(MouseEvent event) {
        getGraphPresenter().onDoubleClick(event);
    }

    @Override
    public void onContextMenuCall(MouseEvent event) {
        getGraphPresenter().onContextMenuCall(event);
    }
}
