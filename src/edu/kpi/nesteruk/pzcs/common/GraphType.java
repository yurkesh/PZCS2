package edu.kpi.nesteruk.pzcs.common;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import edu.kpi.nesteruk.pzcs.graph.generation.GeneratorParams;
import edu.kpi.nesteruk.pzcs.model.primitives.DirectedLink;
import edu.kpi.nesteruk.pzcs.model.queuing.common.QueueConstructor;
import edu.kpi.nesteruk.pzcs.model.queuing.concrete.CriticalPathByNumberOfNodesAndCoherence11;
import edu.kpi.nesteruk.pzcs.model.queuing.concrete.CriticalPathByTimeForAllNodes3;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Anatolii on 2016-03-13.
 */
public enum GraphType {
    TASKS(Localization.getInstance().getLanguage().taskGraph) {
        @Override
        public mxGraphLayout getMxGraphLayout(mxGraph graph) {
            return new mxCompactTreeLayout(graph, false);
        }

        @Override
        GraphPresenter getPresenter(GraphView graphView) {
            return new TasksGraphPresenter(
                    graphView,
                    getGraphStyleSheetInterceptor(),
                    many -> many ? "задачі" : "задачу",
                    TasksGraphModel::new,
                    () -> graphStyle.getNodeSize(this),
                    queueConstructors,
                    new GeneratorParams(1, 5, 6, 0.1, 1, 3)
            );
        }
    },
    SYSTEM(Localization.getInstance().getLanguage().systemGraph) {
        @Override
        public mxGraphLayout getMxGraphLayout(mxGraph graph) {
            return new mxCircleLayout(graph);
        }

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

    public static Collection<QueueConstructor<Task, DirectedLink<Task>>> queueConstructors = Arrays.asList(
            new CriticalPathOfGraphAndNodesByTime1<>(),
            new CriticalPathByTimeForAllNodes3<>(),
            new CriticalPathByNumberOfNodesAndCoherence11()
    );

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

    public abstract mxGraphLayout getMxGraphLayout(mxGraph graph);

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
