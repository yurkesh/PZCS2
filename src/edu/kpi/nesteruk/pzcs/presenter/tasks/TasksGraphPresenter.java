package edu.kpi.nesteruk.pzcs.presenter.tasks;

import com.mxgraph.view.mxStylesheet;
import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.common.GraphDataAssembly;
import edu.kpi.nesteruk.pzcs.graph.generation.Params;
import edu.kpi.nesteruk.pzcs.model.common.GraphModel;
import edu.kpi.nesteruk.pzcs.model.common.GraphModelBundle;
import edu.kpi.nesteruk.pzcs.model.primitives.DirectedLink;
import edu.kpi.nesteruk.pzcs.model.queuing.common.QueueConstructor;
import edu.kpi.nesteruk.pzcs.model.queuing.primitives.CriticalNode;
import edu.kpi.nesteruk.pzcs.model.tasks.Task;
import edu.kpi.nesteruk.pzcs.presenter.common.CaptionsSupplier;
import edu.kpi.nesteruk.pzcs.presenter.common.CommonGraphPresenter;
import edu.kpi.nesteruk.pzcs.presenter.common.GraphVertexSizeSupplier;
import edu.kpi.nesteruk.pzcs.view.common.GraphView;
import edu.kpi.nesteruk.pzcs.view.tasks.GraphGeneratorFrame;
import edu.kpi.nesteruk.pzcs.view.tasks.GraphGeneratorParamsInputDialog;
import edu.kpi.nesteruk.pzcs.view.dialog.Message;
import edu.kpi.nesteruk.util.CollectionUtils;

import java.awt.event.ActionEvent;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Yurii on 2016-03-23.
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

        super(graphView, graphStylesheetInterceptor, captionsSupplier, graphModelFactory, vertexSizeSupplier);
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
            GraphGeneratorFrame generatorFrame = new GraphGeneratorFrame(
                    params,
                    (generatorParams) -> {
                        GraphDataAssembly generated = getModel().generate(generatorParams);
                        System.out.println(generated);
                        setGraph(generated);
                    }
            );
            int frameOffset = generatorsCount * 20;
            generatorFrame.setLocation(frameOffset, frameOffset);
            generatorsCount++;
        } else {
            Message.showMessage(true, "Incorrect params", getParamsErrorCaption(check));
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
                .map(queueConstructor ->
                        queueConstructor.constructQueues(
                                (GraphModelBundle<Task, DirectedLink<Task>>) getModel().getSerializable())
                ).collect(CollectionUtils.CustomCollectors.toMap(
                        titleWithCriticalNodesPair -> titleWithCriticalNodesPair.first,
                        Pair::getSecond,
                        LinkedHashMap::new
                ));

        queuesWithTitles.forEach((title, queues) -> System.out.println(title + ":\n" + queuesToString(queues) + "\n"));
    }

    private static String queuesToString(Collection<CriticalNode<Task>> criticalNodes) {
        StringBuilder sb = new StringBuilder();
        criticalNodes.forEach(node ->
                sb.append(node.node).append(" (").append(node.value).append(")").append("\n")
        );
        return sb.toString();
    }


}
