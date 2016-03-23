package edu.kpi.nesteruk.pzcs.common;

import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxEdgeStyle;
import com.mxgraph.view.mxStylesheet;
import edu.kpi.nesteruk.pzcs.model.system.SystemGraphModel;
import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraphModel;
import edu.kpi.nesteruk.pzcs.presenter.system.SystemGraphPresenter;
import edu.kpi.nesteruk.pzcs.presenter.tasks.TasksGraphPresenter;
import edu.kpi.nesteruk.pzcs.view.common.CommonGraphView;
import edu.kpi.nesteruk.pzcs.presenter.common.CommonGraphPresenter;
import edu.kpi.nesteruk.pzcs.presenter.common.GraphPresenter;
import edu.kpi.nesteruk.pzcs.view.common.GraphView;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Yurii on 2016-03-13.
 */
public enum GraphType {
    TASKS("Tasks") {

        @Override
        GraphPresenter getPresenter(GraphView graphView) {
            return new TasksGraphPresenter(
                    graphView,
                    getGraphStyleSheetInterceptor(),
                    many -> many ? "tasks" : "task",
                    TasksGraphModel::new
            );
        }
    },
    SYSTEM("System") {
        @Override
        GraphPresenter getPresenter(GraphView graphView) {
            return new SystemGraphPresenter(
                    graphView,
                    getGraphStyleSheetInterceptor(),
                    many -> many ? "CPUs" : "CPU",
                    SystemGraphModel::new
            );
        }
    };

    private final String caption;

    GraphType(String caption) {
        this.caption = caption;
    }

    public String getCaption() {
        return caption;
    }


    protected Function<mxStylesheet, mxStylesheet> getGraphStyleSheetInterceptor() {
        return stylesheet -> {
            stylesheet.setDefaultVertexStyle(getCustomVertexStyles(stylesheet.getDefaultVertexStyle()));
            stylesheet.setDefaultEdgeStyle(getCustomEdgeStyles(stylesheet.getDefaultEdgeStyle()));
            return stylesheet;
        };
    }

    protected Map<String, Object> getCustomVertexStyles(Map<String, Object> defaultVertexStyle) {
        Map<String, Object> vertexStyle = new HashMap<>(defaultVertexStyle);
        vertexStyle.put(mxConstants.STYLE_FOLDABLE, false);
        vertexStyle.put(mxConstants.STYLE_RESIZABLE, false);
        return vertexStyle;
    }

    protected Map<String, Object> getCustomEdgeStyles(Map<String, Object> defaultEdgeStyle) {
        Map<String, Object> edgeStyle = new HashMap<>(defaultEdgeStyle);
        edgeStyle.put(mxConstants.STYLE_EDGE, mxEdgeStyle.ElbowConnector);
        edgeStyle.put(mxConstants.STYLE_SHAPE,    mxConstants.SHAPE_CONNECTOR);
        edgeStyle.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
        edgeStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000");
        edgeStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
        edgeStyle.put(mxConstants.STYLE_LABEL_BACKGROUNDCOLOR, "#f0f0f0");
        return edgeStyle;
    }

    abstract GraphPresenter getPresenter(GraphView graphView);

    public CommonGraphView getGraphView() {
        CommonGraphView view = new CommonGraphView();
        view.setLayout(new BorderLayout());
        view.setPresenter(getPresenter(view));
        return view;
    }
}
