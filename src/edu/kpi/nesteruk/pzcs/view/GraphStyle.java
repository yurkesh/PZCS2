package edu.kpi.nesteruk.pzcs.view;

import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxEdgeStyle;
import edu.kpi.nesteruk.misc.Tuple;
import edu.kpi.nesteruk.pzcs.common.GraphType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yurii on 2016-03-29.
 */
public enum GraphStyle {
    Classic {
        @Override
        public Map<String, Object> getVertexStyles(GraphType graphType) {
            Map<String, Object> vertexStyle = super.getVertexStyles(graphType);
            vertexStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
            vertexStyle.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_ELLIPSE);
            vertexStyle.put(mxConstants.STYLE_FILLCOLOR, Views.Tasks.TASK_COLOR);
            return vertexStyle;
        }

        @Override
        public Map<String, Object> getEdgeStyles(GraphType graphType) {
            Map<String, Object> edgeStyle = super.getEdgeStyles(graphType);
            edgeStyle.put(mxConstants.STYLE_EDGE, mxEdgeStyle.ElbowConnector);
            edgeStyle.put(mxConstants.STYLE_ROUNDED, true);
            edgeStyle.put(mxConstants.STYLE_SHAPE,    mxConstants.SHAPE_CONNECTOR);
            edgeStyle.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
            edgeStyle.put(mxConstants.STYLE_STROKECOLOR, Views.Links.EDGE_COLOR);
            edgeStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
            edgeStyle.put(mxConstants.STYLE_LABEL_BACKGROUNDCOLOR, "#f0f0f0");
            if(graphType == GraphType.SYSTEM) {
                edgeStyle.put("endArrow", "none");
            }
            return edgeStyle;
        }
    },
    Alternative {

        private final int diameter = 50;

        @Override
        public Tuple<Integer> getNodeSize(GraphType graphType) {
            return new Tuple<>(diameter, diameter);
        }

        @Override
        public Map<String, Object> getVertexStyles(GraphType graphType) {
            Map<String, Object> vertexStyles = super.getVertexStyles(graphType);
            vertexStyles.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
            vertexStyles.put(mxConstants.STYLE_FILLCOLOR, graphType == GraphType.TASKS ? "#18ffff" : "#76ff03");
            return vertexStyles;
        }
    }
    ;

    public static GraphStyle DEFAULT_STYLE = Classic;

    public Map<String, Object> getVertexStyles(GraphType graphType) {
        HashMap<String, Object> vertexStyle = new HashMap<>();
        vertexStyle.put(mxConstants.STYLE_FOLDABLE, false);
        vertexStyle.put(mxConstants.STYLE_RESIZABLE, false);
        return vertexStyle;
    }

    public Map<String, Object> getEdgeStyles(GraphType graphType) {
        return new HashMap<>();
    }

    public Tuple<Integer> getNodeSize(GraphType graphType) {
        return new Tuple<>(Views.Tasks.TASK_DIAMETER, Views.Tasks.TASK_DIAMETER);
    }
}
