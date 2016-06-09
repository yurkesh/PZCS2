package edu.kpi.nesteruk.pzcs.view.dashboard;

import edu.kpi.nesteruk.misc.Tuple;
import edu.kpi.nesteruk.pzcs.common.GraphType;
import edu.kpi.nesteruk.pzcs.model.system.ProcessorsGraphBundle;
import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraphBundle;
import edu.kpi.nesteruk.pzcs.scheduling.CommonPlannerTesting;
import edu.kpi.nesteruk.pzcs.planning.PlanningParams;
import edu.kpi.nesteruk.pzcs.planning.SchedulingResult;
import edu.kpi.nesteruk.pzcs.planning.params.ProcessorsParams;
import edu.kpi.nesteruk.pzcs.presenter.common.*;
import edu.kpi.nesteruk.pzcs.presenter.system.SystemPresenter;
import edu.kpi.nesteruk.pzcs.presenter.tasks.TasksPresenter;
import edu.kpi.nesteruk.pzcs.view.common.CommonGraphView;
import edu.kpi.nesteruk.pzcs.view.common.GraphView;
import edu.kpi.nesteruk.pzcs.view.print.Table;
import edu.kpi.nesteruk.pzcs.view.print.TableRepresentationBuilder;
import edu.kpi.nesteruk.pzcs.view.processors.PlannedWorkPresenter;
import edu.kpi.nesteruk.pzcs.view.processors.PlanningParamsInputDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Anatolii on 2016-03-13.
 */
public class UnitedGraphsView implements UnitedGraphPresenter {

    private Map<GraphType, GraphView> typedGraphViews = new LinkedHashMap<>();

    private GraphType currentGraph;

    private final JTabbedPane tabbedPane = new JTabbedPane();

    private final DefaultPresenter defaultPresenter = new DefaultPresenter() {
        @Override
        public void onGantDiagram(ActionEvent event) {
            PlannedWorkPresenter presenter = schedulingResult -> {
                Tuple<Table> executionAndTransfersTables = schedulingResult.getExecutionAndTransfersTables();
                /*
                System.out.println(
                        "Planning result:\nExecution:\n" +
                                new TableRepresentationBuilder(executionAndTransfersTables.first, true).getRepresentation()
                                + "\nTransfers:\n" +
                                new TableRepresentationBuilder(executionAndTransfersTables.second, true).getRepresentation()
                );
                */
                GantDiagrmView.showDiagramForProcessors(executionAndTransfersTables.first, "Processors");
                GantDiagrmView.showDiagramForProcessors(executionAndTransfersTables.second, "Transfers");
            };

            TasksGraphBundle tasks = getTasksPresenter().getTasksGraphBundle();
            ProcessorsGraphBundle processors = getSystemPresenter().getProcessorsGraphBundle();
            SchedulingResult schedulingResult = CommonPlannerTesting.makePlanner(PlanningParams.DEFAULT.labWork).getPlannedWork(
                    processors,
                    tasks,
                    new ProcessorsParams(PlanningParams.DEFAULT.numberOfChannels)
            );
            presenter.displaySchedule(schedulingResult);

            /*
            PlanningParamsInputDialog.showDialog(PlanningParams.DEFAULT, params -> {
                TasksGraphBundle tasks = getTasksPresenter().getTasksGraphBundle();
                ProcessorsGraphBundle processors = getSystemPresenter().getProcessorsGraphBundle();
                SchedulingResult schedulingResult = CommonPlannerTesting.makePlanner(params.labWork).getPlannedWork(
                        processors,
                        tasks,
                        new ProcessorsParams(params.numberOfChannels)
                );
                presenter.displaySchedule(schedulingResult);
            });
            */
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

    public TasksPresenter getTasksPresenter() {
        return getGraphPresenter(GraphType.TASKS, TasksPresenter.class);
    }

    public SystemPresenter getSystemPresenter() {
        return getGraphPresenter(GraphType.SYSTEM, SystemPresenter.class);
    }

    @Override
    public void onNew(ActionEvent event) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onOpen(ActionEvent event) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onSave(ActionEvent event) {
        throw new UnsupportedOperationException();
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
    public void onGantDiagram(ActionEvent event) {
        defaultPresenter.onGantDiagram(event);
    }

    @Override
    public void onMakeQueues(ActionEvent event) {
        getTasksPresenter().onMakeQueues(event);
    }

    @Override
    public void onStatistics(ActionEvent event) {
        getSystemPresenter().onStatistics(event);
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

    @Override
    public void setGraph(Object graph) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ProcessorsGraphBundle getProcessorsGraphBundle() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TasksGraphBundle getTasksGraphBundle() {
        throw new UnsupportedOperationException();
    }
}
