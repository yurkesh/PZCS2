package edu.kpi.nesteruk.pzcs.presenter;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.view.mxEdgeStyle;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import edu.kpi.nesteruk.pzcs.model.common.GraphModel;
import edu.kpi.nesteruk.pzcs.model.primitives.IdAndValue;
import edu.kpi.nesteruk.pzcs.model.common.NodeBuilder;
import edu.kpi.nesteruk.pzcs.model.common.LinkBuilder;
import edu.kpi.nesteruk.pzcs.view.Views;
import edu.kpi.nesteruk.pzcs.view.common.GraphView;
import edu.kpi.nesteruk.pzcs.view.dialog.Dialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Yurii on 2016-02-17.
 */
public class CommonPresenter implements GraphPresenter {

    private static final String TAG = "CommonPresenter";

    private final GraphView graphView;
    private final CaptionsSupplier captionsSupplier;

    private final mxGraph graph;
    private final mxGraphComponent graphComponent;
    private final Object parent;

    private final GraphModel model;

    /**
     * {cellId -> cell (vertex|edge) }
     */
    private final Map<String, mxICell> cellsMap = new LinkedHashMap<>();

    public CommonPresenter(GraphView graphView, CaptionsSupplier captionsSupplier, Supplier<GraphModel> graphModelFactory) {
        this.graphView = graphView;
        this.captionsSupplier = captionsSupplier;

        this.graph = new mxGraph() {
            @Override
            public boolean isCellDisconnectable(Object cell, Object terminal, boolean source) {
                return !((mxICell) cell).isEdge();
            }
        };
        parent = graph.getDefaultParent();

        applyGraphSettings();
        initGraphListeners();

        this.graphComponent = new mxGraphComponent(graph);

        model = graphModelFactory.get();

        graphView.setGraphComponent(graphComponent);
    }

    private void initGraphListeners() {
        graph.addListener(mxEvent.CELL_CONNECTED, (sender, evt) -> {
            Map<String, Object> props = evt.getProperties();
            mxCell edge = (mxCell) props.get(PROP_MX_CELL_EDGE);
            mxICell sourceCell = edge.getSource();
            mxICell targetCell = edge.getTarget();
            connectNodes(sourceCell, targetCell, edge);
        });
    }

    private void applyGraphSettings() {
        mxStylesheet stylesheet = new mxStylesheet();
        stylesheet.setDefaultEdgeStyle(getEdgeStyles());
        stylesheet.setDefaultVertexStyle(getVertexStyles());
        graph.setStylesheet(stylesheet);
    }

    private Map<String, Object> getVertexStyles() {
        Map<String, Object> vertexStyle = graph.getStylesheet().getDefaultVertexStyle();
        vertexStyle.put(mxConstants.STYLE_FOLDABLE, false);
        vertexStyle.put(mxConstants.STYLE_RESIZABLE, false);
        return vertexStyle;
    }

    private Map<String, Object> getEdgeStyles() {
        Map<String, Object> edgeStyle = graph.getStylesheet().getDefaultEdgeStyle();
        edgeStyle.put(mxConstants.STYLE_EDGE, mxEdgeStyle.ElbowConnector);
        edgeStyle.put(mxConstants.STYLE_SHAPE,    mxConstants.SHAPE_CONNECTOR);
        edgeStyle.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
        edgeStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000");
        edgeStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
        edgeStyle.put(mxConstants.STYLE_LABEL_BACKGROUNDCOLOR, "#f0f0f0");

        return edgeStyle;
    }

    @Override
    public void onNewGraphEditor(ActionEvent event) {

    }

    @Override
    public void onNewGraphGenerator(ActionEvent event) {

    }

    @Override
    public void onOpenGraph(ActionEvent event) {

    }

    @Override
    public void onSaveGraph(ActionEvent event) {

    }

    @Override
    public void onNewSystem(ActionEvent event) {

    }

    @Override
    public void onOpenSystem(ActionEvent event) {

    }

    @Override
    public void onSaveSystem(ActionEvent event) {

    }

    @Override
    public void onProcessorsParams(ActionEvent event) {

    }

    @Override
    public void onGantDiagram(ActionEvent event) {

    }

    @Override
    public void onStatistics(ActionEvent event) {

    }

    @Override
    public void onAbout(ActionEvent event) {

    }

    @Override
    public void onExit(ActionEvent event) {
        System.exit(0);
    }

    @Override
    public void onDoubleClick(MouseEvent event) {
        addNode(event.getX(), event.getY());
    }

    @Override
    public void onContextMenuCall(MouseEvent event) {
        this.graphView.showMenu(getMenuItemsForClick(event), event.getX(), event.getY());
    }

    void addNode(int x, int y) {
        graph.getModel().beginUpdate();
        try {
            NodeBuilder nodeBuilder = model.getNodeBuilder();
            String supposedId = nodeBuilder.beginBuild();
            Dialog<String> setIdDialog = new Dialog.InputIntegerDialog(
                    "Making new " + captionsSupplier.apply(false),
                    "Set ID of " + captionsSupplier.apply(false),
                    supposedId
            );
            Optional<String> specifiedId = setIdDialog.show();
            if(specifiedId.isPresent()) {
                boolean idIsCorrect = nodeBuilder.setId(specifiedId.get());
                if(idIsCorrect) {
                    if(nodeBuilder.needWeight()) {
                        Dialog.InputIntegerDialog setWeightDialog = new Dialog.InputIntegerDialog(
                                "Making new " + captionsSupplier.apply(false),
                                "Set Weight of " + captionsSupplier.apply(false),
                                String.valueOf(1)
                        );
                        setWeightDialog.showFetchInt().ifPresent(nodeBuilder::setWeight);
                    }
                    nodeBuilder.finishBuild().ifPresent(nodeIdAndValue -> addNode(x, y, nodeIdAndValue));
                }
            }
        } finally {
            graph.getModel().endUpdate();
        }
    }

    private void addNode(int x, int y, IdAndValue idAndValue) {
        mxICell cell = insertVertex(x, y, idAndValue);
        cellsMap.put(idAndValue.id, cell);
    }

    private mxICell insertVertex(int x, int y, IdAndValue nodeIdAndValue) {
        return (mxICell) graph.insertVertex(parent, nodeIdAndValue.id, nodeIdAndValue.value, x, y, Views.Tasks.TASK_DIAMETER, Views.Tasks.TASK_DIAMETER, Views.Tasks.TASK_STYLE);
    }

    private void connectNodes(mxICell sourceCell, mxICell targetCell, mxICell edge) {
        if (sourceCell != null && targetCell != null) {
            String sourceId = sourceCell.getId();
            String targetId = targetCell.getId();

            LinkBuilder linkBuilder = model.getLinkBuilder();
            if(linkBuilder.beginConnect(sourceId, targetId)) {
                if(linkBuilder.needWeight()) {
                    Dialog.InputIntegerDialog setWeightDialog = new Dialog.InputIntegerDialog(
                            "Connecting " + captionsSupplier.apply(true), "Set connection weight:", String.valueOf(1)
                    );
                    setWeightDialog.showFetchInt().ifPresent(linkBuilder::setWeight);
                }
            }

            Optional<IdAndValue> link = linkBuilder.finishConnect();
            if(link.isPresent()) {
                IdAndValue edgeIdAndValue = link.get();
                edge.setId(edgeIdAndValue.id);
                edge.setValue(edgeIdAndValue.value);
                cellsMap.put(edgeIdAndValue.id, edge);
            } else {
                graph.getModel().remove(edge);
            }
        }
    }

    private void deleteNode(int x, int y) {
        deleteCell(x, y).ifPresent(id -> {
            cellsMap.remove(id);
            model.deleteNode(id);
        });
    }

    private Optional<String> deleteCell(int x, int y) {
        mxCell cellToDelete = (mxCell) graphComponent.getCellAt(x, y);
        if(cellToDelete == null) {
            return Optional.empty();
        } else {
            String deletedId = cellToDelete.getId();
            graph.getModel().remove(cellToDelete);
            return Optional.of(deletedId);
        }
    }

    private void deleteLink(int x, int y) {
        deleteCell(x, y).ifPresent(id -> {
            cellsMap.remove(id);
            model.deleteLink(id);
        });
    }

    Collection<JMenuItem> getMenuItemsForClick(MouseEvent event) {
        final int x = event.getX();
        final int y = event.getY();

        ArrayList<JMenuItem> menuOptions = new ArrayList<>();

        menuOptions.add(new JMenuItem(new AbstractAction("Add " + captionsSupplier.apply(false)) {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNode(x, y);
            }
        }));

        mxICell cell = (mxICell) graphComponent.getCellAt(x, y);
        if(cell != null) {
            if(cell.isVertex()) {
                menuOptions.add(new JMenuItem(new AbstractAction("Delete " + captionsSupplier.apply(false)) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        deleteNode(x, y);
                    }
                }));
            } else {
                menuOptions.add(new JMenuItem(new AbstractAction("Delete link") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        deleteLink(x, y);
                    }
                }));
            }
        }

        menuOptions.add(new JMenuItem(new AbstractAction("Validate " + captionsSupplier.apply(true) + " graph topology") {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean valid = model.validate();
                System.out.println("Is "+ captionsSupplier.apply(true) +" graph valid = " + valid);
            }
        }));

        return menuOptions;
    }

    public interface CaptionsSupplier extends Function<Boolean, String> {

        /**
         *
         * @param many - false if caption for single item
         * @return caption (title) of concrete item
         */
        @Override
        String apply(Boolean many);
    }
}