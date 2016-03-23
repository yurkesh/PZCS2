package edu.kpi.nesteruk.pzcs.view.dashboard;

import edu.kpi.nesteruk.pzcs.common.GraphType;
import edu.kpi.nesteruk.pzcs.presenter.common.*;
import edu.kpi.nesteruk.pzcs.view.common.CommonGraphView;
import edu.kpi.nesteruk.pzcs.view.common.GraphView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Yurii on 2016-03-13.
 */
public class UnitedGraphsView implements UnitedGraphPresenter {

    private Map<GraphType, GraphView> typedGraphViews = new LinkedHashMap<>();

    private GraphType currentGraph;

    private final JTabbedPane tabbedPane = new JTabbedPane();

    private final DefaultPresenter defaultPresenter = new DefaultPresenter() {
        @Override
        public void onGantDiagram(ActionEvent event) {

        }

        @Override
        public void onStatistics(ActionEvent event) {

        }

        @Override
        public void onAbout(ActionEvent event) {

        }
    };

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
        tabbedPane.addChangeListener(changeEvent -> currentGraph = graphTypes[tabbedPane.getSelectedIndex()]);
    }

    @Override
    public JComponent getGraphsContainer() {
        return tabbedPane;
    }

    private GraphPresenter getGraphPresenter() {
        return typedGraphViews.get(currentGraph).getPresenter();
    }

    private <T extends GraphPresenter> T getGraphPresenter(GraphType graphType, Class<T> tClass) {
        return tClass.cast(typedGraphViews.get(graphType).getPresenter());
    }

    private TasksPresenter getTasksPresenter() {
        return getGraphPresenter(GraphType.TASKS, TasksPresenter.class);
    }

    private SystemPresenter getSystemPresenter() {
        return getGraphPresenter(GraphType.SYSTEM, SystemPresenter.class);
    }

    @Override
    public void onNewGraphEditor(ActionEvent event) {
        getTasksPresenter().onNewGraphEditor(event);
    }

    @Override
    public void onNewGraphGenerator(ActionEvent event) {
        getTasksPresenter().onNewGraphGenerator(event);
    }

    @Override
    public void onOpenGraph(ActionEvent event) {
        getTasksPresenter().onOpenGraph(event);
    }

    @Override
    public void onSaveGraph(ActionEvent event) {
        getTasksPresenter().onSaveGraph(event);
    }

    @Override
    public void onNewSystem(ActionEvent event) {
        getSystemPresenter().onNewSystem(event);
    }

    @Override
    public void onOpenSystem(ActionEvent event) {
        getSystemPresenter().onOpenSystem(event);
    }

    @Override
    public void onSaveSystem(ActionEvent event) {
        getSystemPresenter().onSaveSystem(event);
    }

    @Override
    public void onProcessorsParams(ActionEvent event) {
        getSystemPresenter().onProcessorsParams(event);
    }

    @Override
    public void onGantDiagram(ActionEvent event) {
        defaultPresenter.onGantDiagram(event);
    }

    @Override
    public void onStatistics(ActionEvent event) {
        defaultPresenter.onStatistics(event);
    }

    @Override
    public void onAbout(ActionEvent event) {
        defaultPresenter.onAbout(event);
    }

    @Override
    public void onExit(ActionEvent event) {
        defaultPresenter.onExit(event);
    }

    @Override
    public void onDoubleClick(MouseEvent event) {
        getGraphPresenter().onDoubleClick(event);
    }

    @Override
    public void onContextMenuCall(MouseEvent event) {
        getGraphPresenter().onContextMenuCall(event);
    }

    @Override
    public void onValidate(ActionEvent event) {
        getGraphPresenter().onValidate(event);
    }
}
