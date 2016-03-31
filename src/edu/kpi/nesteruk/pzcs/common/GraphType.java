package edu.kpi.nesteruk.pzcs.common;

import com.mxgraph.view.mxStylesheet;
import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.model.common.GraphModelBundle;
import edu.kpi.nesteruk.pzcs.model.primitives.DirectedLink;
import edu.kpi.nesteruk.pzcs.model.queuing.common.DefaultPathsConstructor;
import edu.kpi.nesteruk.pzcs.model.queuing.common.QueueConstructor;
import edu.kpi.nesteruk.pzcs.model.queuing.common.NodesQueue;
import edu.kpi.nesteruk.pzcs.model.queuing.concrete.CriticalPathOfGraphAndNodesByTime1;
import edu.kpi.nesteruk.pzcs.model.system.SystemGraphModel;
import edu.kpi.nesteruk.pzcs.model.tasks.Task;
import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraphModel;
import edu.kpi.nesteruk.pzcs.presenter.common.GraphPresenter;
import edu.kpi.nesteruk.pzcs.presenter.system.SystemGraphPresenter;
import edu.kpi.nesteruk.pzcs.presenter.tasks.TasksGraphPresenter;
import edu.kpi.nesteruk.pzcs.view.GraphStyle;
import edu.kpi.nesteruk.pzcs.view.common.CommonGraphView;
import edu.kpi.nesteruk.pzcs.presenter.common.GraphPresenter;
import edu.kpi.nesteruk.pzcs.view.common.GraphView;
import edu.kpi.nesteruk.pzcs.view.localization.Localization;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Anatolii on 2016-03-13.
 */
public enum GraphType {
    TASKS(Localization.getInstance().getLanguage().taskGraph) {

        @Override
        GraphPresenter getPresenter(GraphView graphView) {
            QueueConstructor<Task, DirectedLink<Task>> graphAndNodesByTime = tasksModelBundle ->
                    new CriticalPathOfGraphAndNodesByTime1<Task, DirectedLink<Task>>(true)
                            .constructQueue(tasksModelBundle);

            return new TasksGraphPresenter(
                    graphView,
                    getGraphStyleSheetInterceptor(),
                    many -> many ? "задачі" : "задачу",
                    TasksGraphModel::new,
                    () -> graphStyle.getNodeSize(this),
                    Arrays.asList(graphAndNodesByTime)
            );
        }
    },
    SYSTEM(Localization.getInstance().getLanguage().systemGraph) {
        @Override
        GraphPresenter getPresenter(GraphView graphView) {
            return new SystemGraphPresenter(
                    graphView,
                    getGraphStyleSheetInterceptor(),
                    many -> many ? "CPUs" : "CPU",
                    SystemGraphModel::new,
                    () -> graphStyle.getNodeSize(this)
            );
        }
    };

    private final String caption;
    protected final GraphStyle graphStyle;

    GraphType(String caption, GraphStyle graphStyle) {
        this.caption = caption;
        this.graphStyle = graphStyle;
    }

    GraphType(String caption) {
        this(caption, GraphStyle.DEFAULT_STYLE);
    }

    public String getCaption() {
        return caption;
    }

    protected Function<mxStylesheet, mxStylesheet> getGraphStyleSheetInterceptor() {
        return stylesheet -> {
            Map<String, Object> vertexStyle = new HashMap<>(stylesheet.getDefaultVertexStyle());
            vertexStyle.putAll(graphStyle.getVertexStyles(this));
            stylesheet.setDefaultVertexStyle(vertexStyle);

            Map<String, Object> edgeStyle = new HashMap<>(stylesheet.getDefaultEdgeStyle());
            edgeStyle.putAll(graphStyle.getEdgeStyles(this));
            stylesheet.setDefaultEdgeStyle(edgeStyle);
            return stylesheet;
        };
    }

    abstract GraphPresenter getPresenter(GraphView graphView);

    public CommonGraphView getGraphView() {
        CommonGraphView view = new CommonGraphView();
        view.setLayout(new BorderLayout());
        view.setPresenter(getPresenter(view));
        return view;
    }
}
