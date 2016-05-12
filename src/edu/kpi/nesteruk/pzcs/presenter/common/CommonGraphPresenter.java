package edu.kpi.nesteruk.pzcs.presenter.common;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxIGraphLayout;
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
import edu.kpi.nesteruk.pzcs.common.GraphDataAssembly;
import edu.kpi.nesteruk.pzcs.common.GraphType;
import edu.kpi.nesteruk.pzcs.model.common.GraphModel;
import edu.kpi.nesteruk.pzcs.model.common.GraphModelBundle;
import edu.kpi.nesteruk.pzcs.model.common.LinkBuilder;
import edu.kpi.nesteruk.pzcs.model.common.NodeBuilder;
import edu.kpi.nesteruk.pzcs.model.primitives.IdAndValue;
import edu.kpi.nesteruk.pzcs.view.common.GraphView;
import edu.kpi.nesteruk.util.CollectionUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by Anatolii on 2016-02-17.
 */
public abstract class CommonGraphPresenter implements GraphPresenter {

    private final GraphView graphView;
    private final CaptionsSupplier captionsSupplier;
    private final Supplier<GraphModel> graphModelFactory;
    private final GraphVertexSizeSupplier vertexSizeSupplier;

    private final mxGraph graph;
    private final mxGraphComponent graphComponent;
    private final Object parent;

    private GraphModel model;
    private GraphType graphType;

    private boolean listenCellConnection = true;

    /**
     * {cellId -> nodeId}, {nodeId -> cellId}
     */
    private OneToOneMapper<String, String> cellIdAndNodeIdMapper = new OneToOneMapper<>();
    private mxIGraphLayout graphLayout;

    public CommonGraphPresenter(
            GraphView graphView,
            Function<mxStylesheet, mxStylesheet> graphStylesheetInterceptor,
            CaptionsSupplier captionsSupplier,
            Supplier<GraphModel> graphModelFactory,
            GraphVertexSizeSupplier vertexSizeSupplier,
            GraphType graphType) {

        this.graphType = graphType;
        this.graphView = graphView;
        this.captionsSupplier = captionsSupplier;
        this.graphModelFactory = graphModelFactory;
        this.vertexSizeSupplier = vertexSizeSupplier;

        this.graph = new mxGraph() {

            private boolean isEdge(Object cell) {
                return ((mxICell) cell).isEdge();
            }

            @Override
            public boolean isCellDisconnectable(Object cell, Object terminal, boolean source) {
                return !isEdge(cell);
            }

            @Override
            public boolean isCellEditable(Object cell) {
                return super.isCellEditable(cell);
            }
        };
        parent = graph.getDefaultParent();

        graphLayout = new mxHierarchicalLayout(graph);

        applyGraphViewSettings(graphStylesheetInterceptor);
        initGraphListeners();

        this.graphComponent = new mxGraphComponent(graph);

        initGraphComponentListeners();

        model = graphModelFactory.get();

        graphView.setGraphComponent(graphComponent);
    }

    private void initGraphListeners() {
        graph.addListener(mxEvent.CELL_CONNECTED, connectCellsListener);
    }

    private void initGraphComponentListeners() {
        graphComponent.addListener(mxEvent.START_EDITING, startEditingListener);
        graphComponent.addListener(mxEvent.LABEL_CHANGED, labelChangedListener);
    }

    private void setListenGraphCellConnection(boolean listen) {
        this.listenCellConnection = listen;
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
                "Валідація",
                 "Граф " + (error ? "не" : "") + "валідний"
        );
        if (!error) {
            formatGraph();
        }
    }

    void addNode(int x, int y) {
        graph.getModel().beginUpdate();
        try {
            NodeBuilder nodeBuilder = model.getNodeBuilder();
            String supposedId = nodeBuilder.beginBuild();
            Optional<String> specifiedId = graphView.showStringInputDialog(
                    "New",// + captionsSupplier.getCaption(false),
                    "Id",// + captionsSupplier.getCaption(false),
                    supposedId
            );
            if(specifiedId.isPresent()) {
                boolean idIsCorrect = nodeBuilder.setId(specifiedId.get());
                if(idIsCorrect) {
                    if(nodeBuilder.needWeight()) {
                        graphView.showIntInputDialog(
                                "New",// + captionsSupplier.getCaption(false),
                                "Вага",// + captionsSupplier.getCaption(false),
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
        if(listenCellConnection) {
            Map<String, Object> props = evt.getProperties();
            mxCell edge = (mxCell) props.get(PROP_MX_CELL_EDGE);
            mxICell sourceCell = edge.getSource();
            mxICell targetCell = edge.getTarget();
            connectNodes(sourceCell, targetCell, edge);
        }
    };

    private mxICell tempEdge;
    private final mxEventSource.mxIEventListener startEditingListener = (sender, evt) -> {
        tempEdge = (mxICell) evt.getProperties().get("cell");
    };

    private final mxEventSource.mxIEventListener labelChangedListener = (sender, evt) -> {
        if(tempEdge != null) {
            mxICell edge = (mxICell) evt.getProperties().get("cell");
            if(tempEdge.equals(edge)) {
                String edgeId = edge.getId();
                String idOfLink = cellIdAndNodeIdMapper.getByKey(edgeId);
                String text = (String) evt.getProperties().get("value");
                IdAndValue idAndValue = model.updateWeight(idOfLink, text);
                String updatedIdOfLink = idAndValue.id;
                if(!idOfLink.equals(updatedIdOfLink)) {
                    cellIdAndNodeIdMapper.replace(edgeId, idOfLink, updatedIdOfLink);
                }
                edge.setValue(idAndValue.value);
            } else {
                throw new IllegalStateException("Edit started on another edge. StartCell = " + tempEdge + ", EndCell = " + edge);
            }
        }
        tempEdge = null;
    };

    private void addIdsMappings(IdAndValue nodeIdAndValue, mxICell cell) {
        cellIdAndNodeIdMapper.add(cell.getId(), nodeIdAndValue.id);
    }

    private String removeIdsMappingsByCellId(String deletedCellId) {
        return cellIdAndNodeIdMapper.removeByKey(deletedCellId);
    }

    private mxICell insertVertex(int x, int y, IdAndValue nodeIdAndValue) {
        Tuple<Integer> widthAndHeight = vertexSizeSupplier.getVertexSize();
        return (mxICell) graph.insertVertex(parent, null, nodeIdAndValue.value, x, y, widthAndHeight.getFirst(), widthAndHeight.getSecond());
    }

    private void connectNodes(mxICell sourceCell, mxICell targetCell, mxICell edge) {
        if (sourceCell != null && targetCell != null) {
            String sourceId = sourceCell.getId();
            String targetId = targetCell.getId();

            LinkBuilder linkBuilder = model.getLinkBuilder();
            if(linkBuilder.beginConnect(cellIdAndNodeIdMapper.getByKey(sourceId), cellIdAndNodeIdMapper.getByKey(targetId))) {
                if(linkBuilder.needWeight()) {
                    graphView.showIntInputDialog(
                            "З'єднання",// + captionsSupplier.getCaption(true),
                            "Вага:", 1
                    ).ifPresent(linkBuilder::setWeight);
                }
            }

            Optional<IdAndValue> link = linkBuilder.finishConnect();
            if(link.isPresent()) {
                addEdge(edge, link.get());
            } else {
                graph.getModel().remove(edge);
            }
        }
    }

    private void addEdge(mxICell edge, IdAndValue edgeIdAndValue) {
        edge.setValue(edgeIdAndValue.value);
        addIdsMappings(edgeIdAndValue, edge);
    }

    private void deleteNode(int x, int y) {
        deleteCell(x, y).ifPresent(id -> {
            String idOfRemovedNode = removeIdsMappingsByCellId(id);
            Collection<String> linkIDsToRemove = model.deleteNode(idOfRemovedNode);
            if(!CollectionUtils.isEmpty(linkIDsToRemove)) {
                linkIDsToRemove.stream()
                        //Get id of edge-cell by id of link
                        .map(cellIdAndNodeIdMapper::getByValue)
                        //Get edge-cell by its id
                        .map(this::getCellById)
                        //Remove cells from model
                        .forEach(cell -> graph.getModel().remove(cell));
            }
        });
        System.out.println();
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
                String idOfRemovedEdge = removeIdsMappingsByCellId(id);
                model.deleteLink(idOfRemovedEdge);
            } catch (NullPointerException e) {
                System.err.println("Removed not fully connected link");
            }
        });
    }

    Collection<JMenuItem> getMenuItemsForClick(MouseEvent event) {
        final int x = event.getX();
        final int y = event.getY();

        ArrayList<JMenuItem> menuOptions = new ArrayList<>();

        menuOptions.add(new JMenuItem(new AbstractAction("Додати " + captionsSupplier.getCaption(false)) {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNode(x, y);
            }
        }));

        mxICell cell = (mxICell) graphComponent.getCellAt(x, y);
        if(cell != null) {
            if(cell.isVertex()) {
                menuOptions.add(new JMenuItem(new AbstractAction("Видалити " + captionsSupplier.getCaption(false)) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        deleteNode(x, y);
                    }
                }));
            } else {
                menuOptions.add(new JMenuItem(new AbstractAction("Видалити зв'язок") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        deleteLink(x, y);
                    }
                }));
            }
        }

        return menuOptions;
    }

    @Override
    public void onNew(ActionEvent event) {
        reset(true);
    }

    @Override
    public void onOpen(ActionEvent event) {
        graphView.showFileChooserDialog(false).ifPresent(this::openGraph);
    }

    private void openGraph(String filePath) {
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            GraphModelBundle modelSerializable = (GraphModelBundle) ois.readObject();
            GraphPresenterSerializable presenterSerializable = (GraphPresenterSerializable) ois.readObject();

            model = graphModelFactory.get();
            restoreGraph(model.restore(modelSerializable), presenterSerializable.getNodeIdToItsCoordinates());
        } catch (Exception e) {
            e.printStackTrace();
            graphView.showMessage(true, "Cannot open " + captionsSupplier.getCaption(true) + " graph", e.getMessage());
        }
    }

    protected void formatGraph(){
        graph.getModel().beginUpdate();
        mxGraphLayout mxGraphLayout = graphType.getMxGraphLayout(graph);
//            mxCompactTreeLayout layout = new mxCompactTreeLayout(graph, true);
        mxGraphLayout.execute(graph.getDefaultParent());
        graph.getModel().endUpdate();
    }

    protected void setGraph(GraphDataAssembly graphDataAssembly) {
        reset(false);
        restoreNodes(graphDataAssembly.nodes);
        restoreLinks(graphDataAssembly.links);
        graphLayout.execute(parent);
    }

    private void restoreGraph(GraphDataAssembly restoredModel, Map<String, Tuple<Integer>> nodeIdToItsCoordinates) {
        reset(false);
        restoreNodes(restoredModel.nodes, nodeIdToItsCoordinates);
        restoreLinks(restoredModel.links);
    }

    private void reset(boolean clearModel) {
        if(clearModel) {
            model.reset();
        }
        cellIdAndNodeIdMapper.clear();
        graph.removeCells(graph.getChildVertices(parent));
    }

    private void restoreNodes(Collection<IdAndValue> nodes) {
        nodes.forEach(nodeIdAndValue -> addNode(0, 0, nodeIdAndValue));
    }

    private void restoreNodes(Collection<IdAndValue> nodes, Map<String, Tuple<Integer>> nodeIdToItsCoordinates) {
        nodes.forEach(nodeIdAndValue -> {
            Tuple<Integer> coordinates = nodeIdToItsCoordinates.get(nodeIdAndValue.id);
            addNode(coordinates.first, coordinates.second, nodeIdAndValue);
        });
    }

    private void restoreLinks(Collection<Pair<Pair<String, String>, IdAndValue>> links) {
        setListenGraphCellConnection(false);
        links.forEach(linkInfo -> {
            Pair<String, String> srcAndDestIds = linkInfo.first;
            IdAndValue idAndValueOfLink = linkInfo.second;
            mxICell edge = (mxICell) graph.insertEdge(
                    parent,
                    null,
                    idAndValueOfLink.value,
                    getCellById(cellIdAndNodeIdMapper.getByValue(srcAndDestIds.first)),
                    getCellById(cellIdAndNodeIdMapper.getByValue(srcAndDestIds.second))
            );
            addEdge(edge, idAndValueOfLink);
        });
        setListenGraphCellConnection(true);
        System.out.println();
    }

    @Override
    public void onSave(ActionEvent event) {
        graphView.showFileChooserDialog(true).ifPresent(this::saveGraph);
    }

    private void saveGraph(String filePath) {
        GraphModelBundle modelSerializable = model.getSerializable();
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

    protected GraphModel getModel() {
        return model;
    }
}