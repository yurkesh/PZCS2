package edu.kpi.nesteruk.pzcs.view;

import com.mxgraph.util.mxConstants;

/**
 * Created by Yurii on 21.10.2014.
 */
public class Views {
    public static final int DEFAULT_WINDOW_WIDTH = 600;
    public static final int DEFAULT_WINDOW_HEIGHT = 800;
    public static final int WINDOW_MARGIN_TOP = 100;

    public static final int DEFAULT_NODE_WIDTH = 120;
    public static final int DEFAULT_NODE_HEIGHT = 40;
    public static final int DEFAULT_STATE_DIAMETER = 50;
    public static final String COLOR_ERROR = "#FF0000";
    public static final String COLOR_NORMAL = "#C3D9FF";
    public static final String COLOR_STATE = "#FFBF00";
    public static final String STYLE_CONDITION = "RHOMBUS";
    public static final String STYLE_STATE = "shape=ellipse;perimter=ellipsePerimeter;" + mxConstants.STYLE_FILLCOLOR + '=' + COLOR_STATE;

    public static final int TABLE_ROW_HEIGHT = 18;
}
