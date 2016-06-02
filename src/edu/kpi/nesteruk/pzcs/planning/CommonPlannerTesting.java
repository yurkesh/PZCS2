package edu.kpi.nesteruk.pzcs.planning;

import edu.kpi.nesteruk.misc.Tuple;
import edu.kpi.nesteruk.pzcs.common.LabWork;
import edu.kpi.nesteruk.pzcs.graph.misc.GraphUtils;
import edu.kpi.nesteruk.pzcs.model.common.AbstractGraphModel;
import edu.kpi.nesteruk.pzcs.model.common.GraphModelBundle;
import edu.kpi.nesteruk.pzcs.model.common.LinkBuilder;
import edu.kpi.nesteruk.pzcs.model.common.NodeBuilder;
import edu.kpi.nesteruk.pzcs.model.primitives.DirectedLink;
import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;
import edu.kpi.nesteruk.pzcs.model.queuing.concrete.CriticalPathByTimeForAllNodes3;
import edu.kpi.nesteruk.pzcs.model.queuing.primitives.CriticalNode;
import edu.kpi.nesteruk.pzcs.model.system.ProcessorsGraphBundle;
import edu.kpi.nesteruk.pzcs.model.system.SystemGraphModel;
import edu.kpi.nesteruk.pzcs.model.tasks.Task;
import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraphBundle;
import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraphModel;
import edu.kpi.nesteruk.pzcs.planning.params.DuplexMode;
import edu.kpi.nesteruk.pzcs.planning.params.ProcessorsParams;
import edu.kpi.nesteruk.pzcs.planning.params.TransferParams;
import edu.kpi.nesteruk.pzcs.planning.planner.CommonPlanner;
import edu.kpi.nesteruk.pzcs.planning.planner.SingleTaskHostSearcherFactory;
import edu.kpi.nesteruk.pzcs.planning.planner.Variant4EarliestStartWithoutPrediction;
import edu.kpi.nesteruk.pzcs.planning.processors.ScheduledJobsHolder;
import edu.kpi.nesteruk.pzcs.view.dashboard.DashboardView;
import edu.kpi.nesteruk.pzcs.view.dashboard.UnitedGraphsView;
import edu.kpi.nesteruk.pzcs.view.print.Table;
import edu.kpi.nesteruk.pzcs.view.print.TableRepresentationBuilder;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-05-22.
 */
public class CommonPlannerTesting {

    private static final LabWork LAB_WORK;

    static {
        // TODO: 2016-06-02 Set correct formatting
//        ScheduledJobsHolder.USE_EXTENDED_TRANSFER_FORMATTING;
        // TODO: 2016-06-02 Set correct variants
        SingleTaskHostSearcherFactory.setLabs67Variants(2, 4);
        LAB_WORK = LabWork.LAB_6;
    }

    public static void main(String[] args) {
        TasksGraphBundle tasks = makeTasks();
        ProcessorsGraphBundle processors = makeProcessors();

        SchedulingResult schedulingResult = makePlanner(LAB_WORK).getPlannedWork(
                processors,
                tasks,
                new ProcessorsParams(2, false, DuplexMode.Full, TransferParams.messages())
        );
        Tuple<Table> executionAndTransfersTables = schedulingResult.getExecutionAndTransfersTables();
        System.out.println(
                "Planning result:\nExecution:\n" +
                        new TableRepresentationBuilder(executionAndTransfersTables.first, true).getRepresentation()
                + "\nTransfers:\n" +
                        new TableRepresentationBuilder(executionAndTransfersTables.second, true).getRepresentation()
        );

        DashboardView dashboardView = DashboardView.defaultStart();
        UnitedGraphsView graphPresenter = (UnitedGraphsView) dashboardView.getGraphPresenter();
        graphPresenter.getTasksPresenter().setGraph(tasks);
        graphPresenter.getSystemPresenter().setGraph(processors);
    }

    public static Planner makePlanner(LabWork labWork) {
        return new CommonPlanner(
                processorsGraphBundle -> GraphUtils.makeGraphCheckAllEdgesAdded(
                        SystemGraphModel::newGraph,
                        processorsGraphBundle.getNodesMap().values(),
                        processorsGraphBundle.getLinksMap().values()
                ),
                //Sort processors by coherence (number of links)
                GraphUtils::sortVertexesByCoherence,
                tasksGraphBundle -> GraphUtils.makeGraphCheckAllEdgesAdded(
                        TasksGraphModel::makeGraph,
                        tasksGraphBundle.getNodesMap().values(),
                        tasksGraphBundle.getLinksMap().values()
                ),
                tasksGraphBundle -> new CriticalPathByTimeForAllNodes3<Task, DirectedLink<Task>>()
                        .constructQueues(tasksGraphBundle).second.stream()
                        .map(CriticalNode::getNode)
                        .map(Task::getId)
                        .collect(Collectors.toList())
                ,
                SingleTaskHostSearcherFactory.getSearcher(labWork),
                System.out::println
        );
    }

    private static TasksGraphBundle makeTasks() {
        return makeGraph(new TasksGraphModel(), getTasks());
    }

    private static ProcessorsGraphBundle makeProcessors() {
        return makeGraph(new SystemGraphModel(), getProcessors());
    }

    private static PlannerTestingData getTasks() {
        return new
                ControlWorkExampleCaseTasks()
//                LabDeliveryCase1Tasks()
                ;
    }

    private static PlannerTestingData getProcessors() {
        return new
                ControlWorkExampleCaseProcessors()
//                LabDeliveryCase1Processors()
                ;
    }

    private static <N extends Node, L extends Link<N>, G extends GraphModelBundle<N, L>> G makeGraph(
            AbstractGraphModel<N, L, G> graphModel,
            PlannerTestingData testingData) {
        return makeGraph(
                graphModel,
                testingData.getNodesWithWeightMap(),
                testingData.getNodesLinksMap()
        );
    }

    private static <N extends Node, L extends Link<N>, G extends GraphModelBundle<N, L>> G makeGraph(
            AbstractGraphModel<N, L, G> graphModel,
            Map<Integer, Integer> nodesWeightMap,
            List<Connection> links) {

        NodeBuilder nodeBuilder = graphModel.getNodeBuilder();
        for (Map.Entry<Integer, Integer> nodeIdAndWeight : nodesWeightMap.entrySet()) {
            nodeBuilder.beginBuild();
            nodeBuilder.setId(String.valueOf(nodeIdAndWeight.getKey()));
            nodeBuilder.setWeight(nodeIdAndWeight.getValue());
            nodeBuilder.finishBuild();
        }

        LinkBuilder linkBuilder = graphModel.getLinkBuilder();
        for (Connection connection : links) {
            boolean canConnect = linkBuilder.beginConnect(String.valueOf(connection.src), String.valueOf(connection.dest));
            if(!canConnect) {
                throw new IllegalArgumentException("Cannot make connection = " + connection);
            }
            linkBuilder.setWeight(connection.weight);
            linkBuilder.finishConnect();
        }

        return graphModel.getBundle();
    }


    private interface PlannerTestingData {

        Map<Integer, Integer> getNodesWithWeightMap();

        List<Connection> getNodesLinksMap();

    }

    private static class ControlWorkExampleCaseTasks implements PlannerTestingData {

        @Override
        public Map<Integer, Integer> getNodesWithWeightMap() {
            return new LinkedHashMap<Integer, Integer>() {{
                put(1, 3);
                put(2, 2);
                put(3, 1);
                put(4, 5);
                put(5, 1);
                put(6, 4);
                put(7, 3);
                put(8, 6);
                put(9, 2);
                put(10, 3);
                put(11, 4);
                put(12, 1);
                put(13, 5);
                put(14, 5);
                put(15, 2);
                put(16, 1);
                put(17, 2);
                put(18, 5);
                put(19, 1);
                put(20, 3);
            }};
        }

        @Override
        public List<Connection> getNodesLinksMap() {
            return Arrays.asList(
                    c(1, 14, 3),
                    c(2, 9, 1),
                    c(2, 10, 2),
                    c(3, 14, 3),
                    c(3, 10, 1),
                    c(4, 15, 1),
                    c(5, 11, 4),
                    c(5, 16, 2),
                    c(6, 12, 1),
                    c(6, 13, 2),
                    c(7, 13, 2),
                    c(8, 17, 3),
                    c(9, 14, 3),
                    c(10, 18, 2),
                    c(10, 15, 2),
                    c(11, 15, 1),
                    c(12, 16, 2),
                    c(12, 17, 3),
                    c(13, 17, 1),
                    c(14, 18, 1),
                    c(15, 18, 2),
                    c(15, 19, 3),
                    c(16, 20, 3),
                    c(17, 20, 1)
            );
        }
    }

    private static class ControlWorkExampleCaseProcessors implements PlannerTestingData {

        @Override
        public Map<Integer, Integer> getNodesWithWeightMap() {
            return new LinkedHashMap<Integer, Integer>() {{
                put(1, 1);
                put(2, 1);
                put(3, 1);
                put(4, 1);
                put(5, 1);
                put(6, 1);
                put(7, 1);
                put(8, 1);
            }};
        }

        @Override
        public List<Connection> getNodesLinksMap() {
            return Arrays.asList(
                    c(1, 2),
                    c(1, 3),
                    c(1, 4),
                    c(2, 4),
                    c(2, 6),
                    c(3, 4),
                    c(3, 5),
                    c(4, 5),
                    c(4, 6),
                    c(4, 7),
                    c(5, 6),
                    c(5, 7),
                    c(6, 8),
                    c(7, 8)
            );
        }
    }

    public static class LabDeliveryCase1Tasks implements PlannerTestingData {

        @Override
        public Map<Integer, Integer> getNodesWithWeightMap() {
            return new LinkedHashMap<Integer, Integer>() {{
                put(1, 5);
                put(2, 1);
                put(3, 6);
                put(4, 2);
                put(5, 1);
//                put(6, 2);
            }};
        }

        @Override
        public List<Connection> getNodesLinksMap() {
            return Arrays.asList(
                    c(1, 5, 2),
                    c(2, 5, 8),
                    c(3, 5, 1),
                    c(4, 5, 8)
            );
        }

    }

    private static class LabDeliveryCase1Processors implements PlannerTestingData {

        @Override
        public Map<Integer, Integer> getNodesWithWeightMap() {
            return new LinkedHashMap<Integer, Integer>() {{
                put(1, 1);
                put(2, 1);
                put(3, 1);
                put(4, 1);
            }};
        }

        @Override
        public List<Connection> getNodesLinksMap() {
            return Arrays.asList(
                    c(1, 2),
                    c(2, 3),
                    c(3, 4)
            );
        }
    }

    private static Connection c(int src, int dest, int weight) {
        return new Connection(src, dest, weight);
    }

    private static Connection c(int src, int dest) {
        return new Connection(src, dest, 1);
    }

    private static class Connection {
        public final int src;
        public final int dest;
        public final int weight;

        private Connection(int src, int dest, int weight) {
            this.src = src;
            this.dest = dest;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return "Connection{" +
                    "src=" + src +
                    ", dest=" + dest +
                    ", weight=" + weight +
                    '}';
        }
    }
}