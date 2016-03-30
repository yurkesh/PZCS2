package edu.kpi.nesteruk.pzcs.view.common;

import com.mxgraph.swing.mxGraphComponent;
import edu.kpi.nesteruk.pzcs.presenter.common.GraphPresenter;
import edu.kpi.nesteruk.pzcs.view.dialog.*;
import edu.kpi.nesteruk.pzcs.view.dialog.Dialog;
import edu.kpi.nesteruk.util.CollectionUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Optional;

/**
 * Created by Yurii on 2016-03-13.
 */
public class CommonGraphView extends JPanel implements GraphView {

    private mxGraphComponent graphComponent;

    private GraphPresenter presenter;

    @Override
    public GraphPresenter getPresenter() {
        return presenter;
    }

    public void setPresenter(GraphPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setGraphComponent(mxGraphComponent graphComponent) {
        this.graphComponent = graphComponent;
        graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    presenter.onDoubleClick(e);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                handleShowPopupAction(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                handleShowPopupAction(e);
            }

            private void handleShowPopupAction(final MouseEvent event) {
                if(!event.isPopupTrigger()) {
                    return;
                }

                presenter.onContextMenuCall(event);
            }
        });
        add(this.graphComponent, BorderLayout.CENTER);
    }

    @Override
    public void showMenu(Collection<JMenuItem> jMenuItems, int x, int y) {
        if(CollectionUtils.isEmpty(jMenuItems)) {
            return;
        }

        JPopupMenu menu = new JPopupMenu("Choose action:");
        jMenuItems.forEach(menu::add);
        menu.show(graphComponent.getGraphControl(), x, y);
    }

    @Override
    public Optional<String> showStringInputDialog(String title, String text, String prePopulated) {
        return new Dialog.InputTextDialog(
                title,
                text,
                prePopulated
        ).show();
    }

    @Override
    public Optional<Integer> showIntInputDialog(String title, String text, Integer prePopulated) {
        return new Dialog.InputIntegerDialog(
                title,
                text,
                String.valueOf(prePopulated)
        ).showFetchInt();
    }

    @Override
    public void showMessage(boolean error, String title, String text) {
        JOptionPane.showMessageDialog(
                this,
                text,
                title,
                error ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE
        );
    }

    @Override
    public Optional<String> showFileChooserDialog(boolean save) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int val = save ? fileChooser.showSaveDialog(null) : fileChooser.showOpenDialog(null);
        return val == JFileChooser.APPROVE_OPTION ?
                Optional.of(fileChooser.getSelectedFile().getAbsolutePath())
                : Optional.empty();
    }
}
