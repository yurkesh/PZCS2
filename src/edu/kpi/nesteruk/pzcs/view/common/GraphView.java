package edu.kpi.nesteruk.pzcs.view.common;

import com.mxgraph.swing.mxGraphComponent;
import edu.kpi.nesteruk.pzcs.presenter.common.GraphPresenter;

import javax.swing.*;
import java.util.Collection;
import java.util.Optional;

/**
 * Created by Anatolii on 2016-03-13.
 */
public interface GraphView {

    GraphPresenter getPresenter();

    void setGraphComponent(mxGraphComponent graphComponent);

    void showMenu(Collection<JMenuItem> jMenuItems, int x, int y);

    Optional<String> showStringInputDialog(String title, String text, String prePopulated);

    Optional<Integer> showIntInputDialog(String title, String text, Integer prePopulated);

    void showMessage(boolean error, String title, String text);

    Optional<String> showFileChooserDialog(boolean save);
}
