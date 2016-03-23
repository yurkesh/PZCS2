package edu.kpi.nesteruk.pzcs.presenter.common;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import edu.kpi.nesteruk.misc.OneToOneMapper;
import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.misc.Tuple;
import edu.kpi.nesteruk.pzcs.model.common.GraphModel;
import edu.kpi.nesteruk.pzcs.model.common.GraphModelSerializable;
import edu.kpi.nesteruk.pzcs.model.primitives.IdAndValue;
import edu.kpi.nesteruk.pzcs.model.common.NodeBuilder;
import edu.kpi.nesteruk.pzcs.model.common.LinkBuilder;
import edu.kpi.nesteruk.pzcs.view.Views;
import edu.kpi.nesteruk.pzcs.view.common.GraphView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-02-17.
 */
public abstract class CommonGraphPresenter implements GraphPresenter {

    private final GraphView graphView;
    private final CaptionsSupplier captionsSupplier;
    private final Supplier<GraphModel> graphModelFactory;

    private final mxGraph graph;
    private final mxGraphComponent graphComponent;
    private final Object parent;

    private GraphModel model;

    /**
     * {cellId -> nodeId}, {nodeId -> cellId}
     */
    private OneToOneMapper<String, String> cellIdAndNodeIdMapper = new OneToOneMapper<>();

    public CommonGraphPresenter(
            GraphView graphView,
            Function<mxStylesheet, mxStylesheet> graphStylesheetInterceptor,
            CaptionsSupplier captionsSupplier,
            Supplier<GraphModel> graphModelFactory) {

        this.graphView = graphView;
        this.captionsSupplier = captionsSupplier;
        this.graphModelFactory = graphModelFactory;

        this.graph = new mxGraph() {
            @Override
            public boolean isCellDisconnectable(Object cell, Object terminal, boolean source) {
                return !((mxICell) cell).isEdge();
            }
        };
        parent = graph.getDefaultParent();

        applyGraphViewSettings(graphStylesheetInterceptor);
        initGraphListeners();

        this.graphComponent = new mxGraphComponent(graph);

        model = graphModelFactory.get();

        graphView.setGraphComponent(graphComponent);
    }

    private void initGraphListeners() {
        graph.addListener(mxEvent.CELL_CONNECTED, connectCellsListener);
    }

    private void applyGraphViewSettings(Function<mxStylesheet, mxStylesheet> graphStylesheetInterceptor) {
        graph.setStylesheet(graphStylesheetInterceptor.apply(new mxStylesheet()));
    }

    @Override
    public void onDoubleClick(MouseEvent event) {
        addNode(event.getX(), event.getY());
    }

    @Override
    public void onContextMenuCall(MouseEvent event) {
        this.graphView.showMenu(getMenuItemsForClick(event), event.getX(), event.getY());
    }

    @Override
    public void onValidate(ActionEvent event) {
        boolean error = !model.validate();
        String caption = captionsSupplier.getCaption(true);
        caption = String.valueOf(caption.charAt(0)).toUpperCase() + caption.substring(1);
        graphView.showMessage(
                error,
                "Validation result",
                 caption + " graph is " + (error ? "NOT" : "") + "valid"
        );
    }

    void addNode(int x, int y) {
        graph.getModel().beginUpdate();
        try {
            NodeBuilder nodeBuilder = model.getNodeBuilder();
            String supposedId = nodeBuilder.beginBuild();
            Optional<String> specifiedId = graphView.showStringInputDialog(
                    "Making new " + captionsSupplier.getCaption(false),
                    "Set ID of " + captionsSupplier.getCaption(false),
                    supposedId
            );
            if(specifiedId.isPresent()) {
                boolean idIsCorrect = nodeBuilder.setId(specifiedId.get());
                if(idIsCorrect) {
                    if(nodeBuilder.needWeight()) {
                        graphView.showIntInputDialog(
                                "Making new " + captionsSupplier.getCaption(false),
                                "Set Weight of " + captionsSupplier.getCaption(false),
                                1
                        ).ifPresent(nodeBuilder::setWeight);
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
        addIdsMappings(idAndValue, cell);
    }

    private final mxEventSource.mxIEventListener connectCellsListener = (sender, evt) -> {
        Map<String, Object> props = evt.getProperties();
        mxCell edge = (mxCell) props.get(PROP_MX_CELL_EDGE);
        mxICell sourceCell = edge.getSource();
        mxICell targetCell = edge.getTarget();
        connectNodes(sourceCell, targetCell, edge);
    };

    private void addIdsMappings(IdAndValue nodeIdAndValue, mxICell cell) {
        cellIdAndNodeIdMapper.add(cell.getId(), nodeIdAndValue.id);
    }

    private void removeIdsMappingsByCellId(String deletedCellId) {
        cellIdAndNodeIdMapper.removeByKey(deletedCellId);
    }

    private mxICell insertVertex(int x, int y, IdAndValue nodeIdAndValue) {
        return (mxICell) graph.insertVertex(parent, null, nodeIdAndValue.value, x, y, Views.Tasks.TASK_DIAMETER, Views.Tasks.TASK_DIAMETER, Views.Tasks.TASK_STYLE);
    }

    private void connectNodes(mxICell sourceCell, mxICell targetCell, mxICell edge) {
        if (sourceCell != null && targetCell != null) {
            String sourceId = sourceCell.getId();
            String targetId = targetCell.getId();

            LinkBuilder linkBuilder = model.getLinkBuilder();
            if(linkBuilder.beginConnect(cellIdAndNodeIdMapper.getByKey(sourceId), cellIdAndNodeIdMapper.getByKey(targetId))) {
                if(linkBuilder.needWeight()) {
                    graphView.showIntInputDialog(
                            "Connecting " + captionsSupplier.getCaption(true), "Set connection weight:", 1
                    ).ifPresent(linkBuilder::setWeight);
                }
            }

            Optional<IdAndValue> link = linkBuilder.finishConnect();
            if(link.isPresent()) {
                IdAndValue edgeIdAndValue = link.get();
                edge.setValue(edgeIdAndValue.value);
                addIdsMappings(edgeIdAndValue, edge);
            } else {
                graph.getModel().remove(edge);
            }
        }
    }

    private void deleteNode(int x, int y) {
        deleteCell(x, y).ifPresent(id -> {
            removeIdsMappingsByCellId(id);
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
            try {
                removeIdsMappingsByCellId(id);
            } catch (NullPointerException e) {
                System.err.println("Removed not fully connected link");
            }
            model.deleteLink(id);
        });
    }

    Collection<JMenuItem> getMenuItemsForClick(MouseEvent event) {
        final int x = event.getX();
        final int y = event.getY();

        ArrayList<JMenuItem> menuOptions = new ArrayList<>();

        menuOptions.add(new JMenuItem(new AbstractAction("Add " + captionsSupplier.getCaption(false)) {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNode(x, y);
            }
        }));

        mxICell cell = (mxICell) graphComponent.getCellAt(x, y);
        if(cell != null) {
            if(cell.isVertex()) {
                menuOptions.add(new JMenuItem(new AbstractAction("Delete " + captionsSupplier.getCaption(false)) {
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

        return menuOptions;
    }

    protected void onNew(ActionEvent event) {

    }

    protected void onOpen(ActionEvent event) {
        graphView.showFileChooserDialog().ifPresent(this::openGraph);
    }

    private void openGraph(String filePath) {
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            GraphModelSerializable modelSerializable = (GraphModelSerializable) ois.readObject();
            GraphPresenterSerializable presenterSerializable = (GraphPresenterSerializable) ois.readObject();

            model = graphModelFactory.get();
            restoreGraph(model.restore(modelSerializable), presenterSerializable.getNodeIdToItsCoordinates());
        } catch (Exception e) {
            e.printStackTrace();
            graphView.showMessage(true, "Cannot open " + captionsSupplier.getCaption(true) + " graph", e.getMessage());
        }
    }

    private void restoreGraph(Pair<Collection<IdAndValue>, Collection<Pair<Pair<String, String>, IdAndValue>>> restoredModel, Map<String, Tuple<Integer>> nodeIdToItsCoordinates) {
        restoreNodes(restoredModel.first, nodeIdToItsCoordinates);
        restoreLinks(restoredModel.second);
    }

    private void restoreNodes(Collection<IdAndValue> nodes, Map<String, Tuple<Integer>> nodeIdToItsCoordinates) {
        graph.getModel().beginUpdate();
        nodes.forEach(nodeIdAndValue -> {
            Tuple<Integer> coordinates = nodeIdToItsCoordinates.get(nodeIdAndValue.id);
            addNode(coordinates.first, coordinates.second, nodeIdAndValue);
        });
        graph.getModel().endUpdate();
    }

    private void restoreLinks(Collection<Pair<Pair<String, String>, IdAndValue>> links) {
        graph.getModel().beginUpdate();
        links.forEach(linkInfo -> {
            Pair<String, String> srcAndDestIds = linkInfo.first;
            IdAndValue idAndValueOfLink = linkInfo.second;
            graph.insertEdge(
                    parent,
                    null,
                    idAndValueOfLink.value,
                    getCellById(cellIdAndNodeIdMapper.getByValue(srcAndDestIds.first)),
                    getCellById(cellIdAndNodeIdMapper.getByValue(srcAndDestIds.second))
            );
        });
        graph.getModel().endUpdate();
    }

    protected void onSave(ActionEvent event) {
        graphView.showFileChooserDialog().ifPresent(this::saveGraph);
    }

    private void saveGraph(String filePath) {
        GraphModelSerializable modelSerializable = model.getSerializable();
        LinkedHashSet<String> nodesIds = modelSerializable.getNodesIds();
        Map<String, Tuple<Integer>> nodeIdToCoordinates = nodesIds.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        nodeId -> {
                            mxGeometry geometry = getCellById(cellIdAndNodeIdMapper.getByValue(nodeId)).getGeometry();
                            return new Tuple<>((int) Math.round(geometry.getX()), (int) Math.round(geometry.getY()));
                        }
                ));

        GraphPresenterSerializable presenterSerializable = new GraphPresenterSerializable(nodeIdToCoordinates);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(modelSerializable);
            oos.writeObject(presenterSerializable);
        } catch (IOException e) {
            e.printStackTrace();
            graphView.showMessage(true, "Cannot save " + captionsSupplier.getCaption(true) + " graph", e.getMessage());
        }
    }

    private mxICell getCellById(String id) {
        return (mxICell) ((mxGraphModel) graph.getModel()).getCell(id);
    }
}