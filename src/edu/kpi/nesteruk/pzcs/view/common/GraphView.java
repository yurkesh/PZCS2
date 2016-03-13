package edu.kpi.nesteruk.pzcs.view.common;

import com.mxgraph.swing.mxGraphComponent;

import javax.swing.*;
import java.util.Collection;

/**
 * Created by Yurii on 2016-03-13.
 */
public interface GraphView {

    GraphPresenter getPresenter();

    void setGraphComponent(mxGraphComponent graphComponent);

    void showMenu(Collection<JMenuItem> jMenuItems, int x, int y);
}
