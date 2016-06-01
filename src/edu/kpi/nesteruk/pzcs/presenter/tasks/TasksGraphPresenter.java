package edu.kpi.nesteruk.pzcs.presenter.tasks;

import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.view.mxStylesheet;
import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.common.GraphType;
import edu.kpi.nesteruk.pzcs.common.GraphDataAssembly;
import edu.kpi.nesteruk.pzcs.graph.generation.Params;
import edu.kpi.nesteruk.pzcs.graph.misc.GraphUtils;
import edu.kpi.nesteruk.pzcs.model.common.GraphModel;
import edu.kpi.nesteruk.pzcs.model.primitives.DirectedLink;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;
import edu.kpi.nesteruk.pzcs.model.queuing.common.QueueConstructor;
import edu.kpi.nesteruk.pzcs.model.queuing.primitives.CriticalNode;
import edu.kpi.nesteruk.pzcs.model.tasks.Task;
import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraphBundle;
import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraphModel;
import edu.kpi.nesteruk.pzcs.presenter.common.CaptionsSupplier;
import edu.kpi.nesteruk.pzcs.presenter.common.CommonGraphPresenter;
import edu.kpi.nesteruk.pzcs.presenter.common.GraphVertexSizeSupplier;
import edu.kpi.nesteruk.pzcs.view.common.GraphView;
import edu.kpi.nesteruk.pzcs.view.dashboard.TableView;
import edu.kpi.nesteruk.pzcs.view.tasks.GraphGeneratorFrame;
import edu.kpi.nesteruk.pzcs.view.tasks.GraphGeneratorParamsInputDialog;
import edu.kpi.nesteruk.pzcs.view.dialog.Message;
import edu.kpi.nesteruk.util.CollectionUtils;

import java.awt.event.ActionEvent;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by Anatolii on 2016-03-23.
 */
public class TasksGraphPresenter extends CommonGraphPresenter implements TasksPresenter {

    private final Collection<QueueConstructor<Task, DirectedLink<Task>>> queueConstructors;
    private final Params defaultGeneratorParams;

    private int generatorsCount = 0;

    public TasksGraphPresenter(
            GraphView graphView,
            Function<mxStylesheet, mxStylesheet> graphStylesheetInterceptor,
            CaptionsSupplier captionsSupplier,
            Supplier<GraphModel> graphModelFactory,
            GraphVertexSizeSupplier vertexSizeSupplier,
            Collection<QueueConstructor<Task, DirectedLink<Task>>> queueConstructors,
            Params defaultGeneratorParams) {

        super(graphView, graphStylesheetInterceptor, captionsSupplier, graphModelFactory, vertexSizeSupplier, GraphType.TASKS);
        this.queueConstructors = queueConstructors;
        this.defaultGeneratorParams = defaultGeneratorParams;
    }

    @Override
    public void onNewGraphGenerator(ActionEvent event) {
        GraphGeneratorParamsInputDialog.showDialog(defaultGeneratorParams).ifPresent(this::checkAndGenerateWithParams);
    }

    private void checkAndGenerateWithParams(Params params) {
        Params.CheckResult check = Params.isCorrect(params);
        if(check == Params.CheckResult.OK) {
            /*
            GraphGeneratorFrame generatorFrame = new GraphGeneratorFrame(
                    params,
                    (generatorParams) -> {
                        GraphModel model = getModel();
                        GraphDataAssembly generated = ((TasksGraphModel) model).generate(generatorParams);
                        System.out.println(generated);
                        setGraph(generated);
                    }
            );
            */
            GraphDataAssembly generated = getModel().generate(params);
            System.out.println(generated);
            setGraph(generated);
//            double graphCorrelation = GraphUtils.getGraphCorrelation(getModel().getNodesMap().values(), getModel().getLinksMap().values());
//            System.out.println(graphCorrelation);
            int frameOffset = generatorsCount * 20;
            /*
            generatorFrame.setLocation(frameOffset, frameOffset);
            */
            generatorsCount++;
//            formatGraph();
        } else {
            Message.showMessage(true, "Incorrect params", getParamsErrorCaption(check));
        }
    }



    @Override
    public void setGraph(Object graph) {
        if(graph instanceof TasksGraphBundle) {
            setGraph(getModel().restore((TasksGraphBundle) graph));
        } else {
            throw new IllegalArgumentException("Cannot set graph = " + graph);
        }
    }

    private static String getParamsErrorCaption(Params.CheckResult check) {
        switch (check) {
            case MIN_NODE_WEIGHT_LESS_THAN_ZERO:
                return "Min weight of node cannot be less than 0";
            case MAX_NODE_WEIGHT_LESS_THAN_MIN:
                return "Max weight of node cannot be less than min";
            case NUMBER_OF_NODES_LESS_THAN_ONE:
                return "Number of nodes cannot be less than 1";
            case INCORRECT_COHERENCE:
                return "Illegal value of coherence: only > 0 and < 1 allowed";
            case MIN_LINK_WEIGHT_LESS_THAN_ZERO:
                return "Min weight of link cannot be less than 0";
            case MAX_LINK_WEIGHT_LESS_THAN_MIN:
                return "Max weight of link cannot be less than min";
            default:
                throw new IllegalArgumentException("Unknown CheckResult = " + check);
        }
    }

    @Override
    public void onMakeQueues(ActionEvent event) {
        Map<String, List<CriticalNode<Task>>> queuesWithTitles = queueConstructors.stream()
                .map(queueConstructor -> queueConstructor.constructQueues(
                        (TasksGraphBundle) getModel().getBundle()
                ))
                .collect(CollectionUtils.CustomCollectors.toMap(
                        titleWithCriticalNodesPair -> titleWithCriticalNodesPair.first,
                        Pair::getSecond,
                        LinkedHashMap::new
                ));

        queuesWithTitles.forEach((title, queues) -> System.out.println(title + ":\n" + queuesToString(queues) + "\n"));
        queuesWithTitles.forEach((title, queues) -> TableView.showTable(title, queues));
    }

    private static String queuesToString(Collection<CriticalNode<Task>> criticalNodes) {
        StringBuilder sb = new StringBuilder();
        criticalNodes.forEach(node ->
                sb.append(node.node).append(" (").append(node.value).append(")").append("\n")
        );
        return sb.toString();
    }


}
