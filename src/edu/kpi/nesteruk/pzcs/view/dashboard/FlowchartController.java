//package edu.kpi.nesteruk.pzcs.view.dashboard;
//
//import com.mxgraph.model.mxCell;
//import com.mxgraph.model.mxGeometry;
//import com.mxgraph.model.mxICell;
//import com.mxgraph.swing.mxGraphComponent;
//import com.mxgraph.util.*;
//import com.mxgraph.view.mxEdgeStyle;
//import com.mxgraph.view.mxGraph;
//import com.mxgraph.view.mxStylesheet;
//import editor.Views;
//import editor.flowchart.model.*;
//import editor.flowchart.view.Dialog;
//import editor.flowchart.view.FlowchartEditor;
//import editor.stategraph.controller.StateController;
//import editor.stategraph.model.State;
//import editor.stategraph.model.Transfer;
//import editor.stategraph.view.StateChartEditor;
//
//import java.awt.event.MouseEvent;
//import java.io.*;
//import java.util.*;
//
///**
// * Created by Yurii on 09.09.2014.
// */
//public class FlowchartController {
//
//    private static double width = Views.DEFAULT_NODE_WIDTH;
//    private static double height = Views.DEFAULT_NODE_HEIGHT;
//
//    private static double stateDiameter = Views.DEFAULT_NODE_WIDTH / 6.0;
//    private static double portWidth = Views.DEFAULT_NODE_WIDTH / 6.0;
//    private static double portHeight = 6.0;
//
//    private FlowchartEditor flowchartEditor;
//
//    private static mxGraph graph;
//    private Object parent;
//    private mxGraphComponent graphComponent;
//
//    private Node beginNode;
//    private Node endNode;
//
//    private static List<Connection<mxICell>> portsConnections = new LinkedList<>();
//
//    private String[][] matrix;
//
//    private static FlowchartController instance;
//
//    private Node tempNode;
//
//    private StateController stateController;
//
//    public FlowchartController(FlowchartEditor flowchartEditor) {
//        this.flowchartEditor = flowchartEditor;
//
//        graph = new mxGraph() {
//            public boolean isCellFoldable(Object cell, boolean collapse) {
//                return false;
//            }
//        };
//
//        setStyles(graph);
//
//        parent = graph.getDefaultParent();
//        graphComponent = new mxGraphComponent(graph);
//        stateController = new StateController();
//
//        flowchartEditor.setGraphComponent(graphComponent);
//
//        instance = this;
//
//        graph.addListener(mxEvent.CELL_CONNECTED, new mxEventSource.mxIEventListener() {
//            @Override
//            public void invoke(Object sender, mxEventObject evt) {
//                Map<String, Object> props = evt.getProperties();
//                mxCell edge = (mxCell) props.get("edge");
//                mxICell sourceMxPort = edge.getSource();
//                mxICell targetMxPort = edge.getTarget();
//
//                if (sourceMxPort != null && targetMxPort != null) {
//                    Connection<mxICell> connection = new Connection<>(sourceMxPort, targetMxPort);
//
//                    if (!portsConnections.contains(connection)) {
//                        Port sourcePort = (Port) sourceMxPort.getValue();
//                        Port targetPort = (Port) targetMxPort.getValue();
//                        PortType sourcePortType = sourcePort.portType;
//                        PortType targetPortType = targetPort.portType;
//
//                        if (targetPortType == PortType.Input && sourcePortType.canConnect(targetPortType)) {
//                            mxICell sourceCell = sourceMxPort.getParent();
//                            mxICell targetCell = targetMxPort.getParent();
//
//                            if (!sourceCell.equals(targetCell)) {
//                                Node sourceNode = (Node) sourceCell.getValue();
//                                Node targetNode = (Node) targetCell.getValue();
//
//                                if (sourceNode.nodeType == Node.NodeType.Condition) {
//                                    if(sourcePortType == PortType.FalseOut && sourceNode.getConnectedToFalse() == null ||
//                                            sourcePortType == PortType.TrueOut && sourceNode.getConnectedToTrue() == null) {
//                                        sourceNode.connectTo(targetNode, sourcePortType == PortType.TrueOut);
//                                        if(targetNode == endNode) {
//                                            constructState(targetMxPort, targetNode);
//                                        }
//                                        portsConnections.add(connection);
//                                        return;
//                                    }
//                                } else {
//                                    if (sourceNode.getConnected() == null) {
//                                        sourceNode.connectTo(targetNode);
//                                        constructState(targetMxPort, targetNode);
//                                        portsConnections.add(connection);
//                                        return;
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    graph.getModel().remove(edge);
//                }
//            }
//        });
//
//        graphComponent.addListener(mxEvent.LABEL_CHANGED, new mxEventSource.mxIEventListener() {
//            @Override
//            public void invoke(Object sender, mxEventObject evt) {
//                if (tempNode != null) {
//                    mxCell cell = (mxCell) evt.getProperties().get("cell");
//                    if (tempNode.canChangeLabel()) {
//                        tempNode.text = (String) evt.getProperties().get("value");
//                    }
//                    cell.setValue(tempNode);
//                    tempNode = null;
//                }
//            }
//        });
//
//        graphComponent.addListener(mxEvent.START_EDITING, new mxEventSource.mxIEventListener() {
//            @Override
//            public void invoke(Object sender, mxEventObject evt) {
//                tempNode = (Node) ((mxCell) evt.getProperties().get("cell")).getValue();
//            }
//        });
//
//    }
//
//    private static FlowchartController getInstance() {
//        return instance;
//    }
//
//    private void setStyles(mxGraph graph) {
//        mxStylesheet stylesheet = graph.getStylesheet();
//        Map<String, Object> style = stylesheet.getDefaultEdgeStyle();
//        style.put(mxConstants.STYLE_EDGE, mxEdgeStyle.SegmentConnector);
//
//        Hashtable<String, Object> rhombus = new Hashtable<>();
//        rhombus.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RHOMBUS);
//        stylesheet.putCellStyle(Views.STYLE_CONDITION, rhombus);
//    }
//
//    private void setCellAttribute(mxICell cell, String attr, String value) {
//        graph.setCellStyles(
//                attr,
//                value,
//                new Object[]{cell}
//        );
//    }
//
//    private void setCellColor(mxICell cell, String color) {
//        setCellAttribute(cell, mxConstants.STYLE_FILLCOLOR, color);
//    }
//
//    private void highlightCell(mxCell cell, boolean alert) {
//        setCellColor(cell, alert ? Views.COLOR_ERROR : Views.COLOR_NORMAL);
//    }
//
//    private void displayPortAsState(mxICell cell, boolean isState) {
//        double width = isState ? stateDiameter : portWidth;
//        double height = isState ? stateDiameter : portHeight;
//
//        mxGeometry geometry = cell.getGeometry();
//        geometry.setWidth(width);
//        geometry.setHeight(height);
//        geometry.setOffset(new mxPoint(-width/2, -height/(isState ? 1 : 2)));
//
//        setCellColor(cell, isState ? Views.COLOR_STATE : Views.COLOR_NORMAL);
//    }
//
//    private mxCell insertVertex(Node node, int x, int y, String style) {
//        return (mxCell) graph.insertVertex(parent, null, node, x, y, width, height, style);
//    }
//
//    public void addBegin(int x, int y) {
//        if (beginNode == null) {
//            beginNode = new Node(Node.NodeType.Begin, "Початок");
//            addNode(beginNode, x, y);
//        }
//    }
//
//    public void addEnd(int x, int y) {
//        if (endNode == null) {
//            endNode = new Node(Node.NodeType.End, "Кінець");
//            addNode(endNode, x, y);
//        }
//    }
//
//    private mxCell constructPort(Port port) {
//        PortType type = port.portType;
//        mxGeometry portGeo = new mxGeometry(type.x, type.y, portWidth, portHeight);
//        portGeo.setOffset(new mxPoint(-portWidth / 2.0, -portHeight / 2.0));
//        portGeo.setRelative(true);
//
//        mxCell mxPort = new mxCell(type, portGeo, "shape=ellipse;perimter=ellipsePerimeter");
//        mxPort.setVertex(true);
//        mxPort.setValue(port);
//        port.setCell(mxPort);
//        return mxPort;
//    }
//
//    private void constructState(mxICell port, Node stateNode) {
//        displayPortAsState(port, true);
//        stateController.setStateFor(stateNode);
//    }
//
//    private void addPorts(mxCell cell, Node node) {
//        cell.setConnectable(false);
//        for (Port port : node.getPorts()) {
//            graph.addCell(constructPort(port), cell);
//        }
//    }
//
//    private List<Node> nodes = new LinkedList<>();
//    private Map<Node, mxCell> nodesToCells = new HashMap<>();
//
//    public mxCell addNode(Node node, int x, int y) {
//        mxCell cell;
//        graph.getModel().beginUpdate();
//        try {
//            cell = insertVertex(node, x, y, node.nodeType.style);
//            node.cell = cell;
//            addPorts(cell, node);
//            nodes.add(node);
//            nodesToCells.put(node, cell);
//        } finally {
//            graph.getModel().endUpdate();
//        }
//        return cell;
//    }
//
//    public void delete(int x, int y) {
//        mxCell cellToDelete = (mxCell) graphComponent.getCellAt(x, y);
//        Node nodeToDelete = (Node) cellToDelete.getValue();
//
//        for (Node node : nodes) {
//            node.disconnectFrom(nodeToDelete);
//        }
//
//        nodes.remove(nodeToDelete);
//        if (nodeToDelete == beginNode) {
//            beginNode = null;
//        }
//        if (nodeToDelete == endNode) {
//            endNode = null;
//        }
//
//        stateController.remove(nodeToDelete.getState());
//
//        graph.getModel().remove(cellToDelete);
//    }
//
//    public boolean isTargeted(Node nodeToCheck) {
//        for (Node node : nodes) {
//            if(node.isConnectedWith(nodeToCheck)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public void processGraph() {
//        if (beginNode != null && endNode != null) {
//            matrix = new String[nodes.size()][nodes.size()];
//            for (int i = 0; i < matrix.length; i++) {
//                for (int j = 0; j < matrix[0].length; j++) {
//                    matrix[i][j] = ".";
//                }
//            }
//
//            nodes.remove(beginNode);
//            nodes.add(0, beginNode);
//
//            processedNodes = new ArrayList<>(nodes.size());
//            lighted = new LinkedHashSet<>();
//            stateController.clearConnections();
//
//            Node first = beginNode.getConnected();
//            boolean result = processNode(first, beginNode, null, first.getState(), new Transfer.Wrapper(null));
//            System.out.println("RESULT = " + result);
//
//            if (!result) {
//                for (int i = 0; i < nodes.size(); i++) {
//                    highlightCell(nodes.get(i).cell, lighted.contains(i));
//                }
//            } else {
//                printMatrix(matrix);
//                System.out.println("\n\n");
//                new StateChartEditor(stateController.getStatesKeeper());
//            }
//        }
//    }
//
//    private List<Node> processedNodes = new ArrayList<>(nodes.size());
//    private Set<Integer> lighted = new LinkedHashSet<>();
//
//    private boolean processNode(Node node, Node from,
//                                Boolean conditionOfTransfer, State fromState,
//                                Transfer.Wrapper intermediate) {
//        System.out.println("Processing " + node);
//        if (node != null) {
//
//            State state = processState(node, from, fromState, conditionOfTransfer, intermediate);
//
//            if (!processedNodes.contains(node)) {
//                processedNodes.add(node);
//
//                if (node.nodeType != Node.NodeType.Condition) {
//                    int nodeIndex = nodes.indexOf(node);
//                    int fromIndex = nodes.indexOf(from);
//
//                    matrix[fromIndex][nodeIndex] = convertBooleanConditionIntoString(conditionOfTransfer);
//                    Node connectedNode = node.getConnected();
//
//                    if (connectedNode != null) {
//                        int connectedIndex = nodes.indexOf(connectedNode);
//                        matrix[nodeIndex][connectedIndex] = "-";
//                        if (connectedNode == endNode) {
//                            processState(endNode, node, state, null, intermediate);
//                            return true;
//                        }
//                        return processNode(node.getConnected(), node, null, state, intermediate);
//                    }
//                } else {
//                    Node trueNode = node.getConnectedToTrue();
//                    Node falseNode = node.getConnectedToFalse();
//
//                    int index = nodes.indexOf(node);
//                    int indexOfTrue = nodes.indexOf(trueNode);
//                    int indexOfFalse = nodes.indexOf(falseNode);
//
//                    matrix[index][indexOfTrue] = convertBooleanConditionIntoString(true);
//                    matrix[index][indexOfFalse] = convertBooleanConditionIntoString(false);
//
//                    boolean result = false;
//
//                    if (trueNode == endNode) {
//                        processState(endNode, node, state, true, intermediate);
//                        result = true;
//                    } else {
//                        result = processNode(trueNode, node, true, state, intermediate);
//                    }
//                    if (falseNode == endNode) {
//                        processState(endNode, node, state, false, intermediate);
//                        result = true;
//                    } else {
//                        result |= processNode(falseNode, node, false, state, intermediate);
//                    }
//                    return result;
//                }
//            } else {
//                System.out.println("processedNodes.contains(" + node + ")");
//                lighted.add(nodes.indexOf(node));
//                return false;
//            }
//        }
//        return false;
//    }
//
//    private State processState(Node node, Node from,
//                                               State fromState, Boolean conditionOfTransfer,
//                                               Transfer.Wrapper intermediate) {
//        State currentState = node.getState();
//
//
//        if (from != beginNode) {
//
//            String condition;
//            String signals;
//            if(from.nodeType == Node.NodeType.Condition) {
//                condition = conditionOfTransfer != null ? (conditionOfTransfer ? from.text : "!" + from.text) : "-";
//                signals = "";
//            } else {
//                condition = "";
//                signals = from.text;
//            }
//
//            if(currentState != null) {
//                if(intermediate.transfer == null) {
//                    stateController.connect(fromState, currentState, condition, signals);
//                } else {
//                    stateController.supplementTransfer(intermediate.transfer, condition, signals);
//                    stateController.connect(currentState, intermediate.transfer);
//                    intermediate.transfer = null;
//                }
//            } else {
//                if(intermediate.transfer == null) {
//                    intermediate.transfer = stateController.beginConnect(fromState, condition, signals);
//                } else {
//                    stateController.supplementTransfer(intermediate.transfer, condition, signals);
//                }
//            }
//        }
//        return currentState != null ? currentState : fromState;
//    }
//
//    private static String convertBooleanConditionIntoString(Boolean conditionOfTransfer) {
//        return conditionOfTransfer != null ? (conditionOfTransfer ? "1" : "0") : "-";
//    }
//
//    private static String fileText;
//
//    private void printMatrix(String[][] matrix) {
//        StringBuilder printBuilder = new StringBuilder("MATRIX\n");
//        StringBuilder fileBuilder = new StringBuilder();
//
//        printBuilder.append(format(" "));
//        Node[] nodesArray = new Node[nodes.size()];
//        nodes.toArray(nodesArray);
//
//
//        mxGeometry tempGeo = null;
//        for (int i = 0; i < nodesArray.length; i++) {
//            printBuilder.append(format(nodesArray[i]));
//            if (nodesArray[i].nodeType == Node.NodeType.Condition) {
//                fileBuilder.append('-');
//            } else if (nodesArray[i].nodeType == Node.NodeType.End) {
//                fileBuilder.append('~');
//            }
//            fileBuilder.append(nodesArray[i]);
//            tempGeo = nodesToCells.get(nodesArray[i]).getGeometry();
//            fileBuilder.append(':').append((int) tempGeo.getX()).append(',').append((int) tempGeo.getY());
//            if (i != nodesArray.length - 1) {
//                fileBuilder.append(';');
//            }
//        }
//        printBuilder.append("\n");
//        fileBuilder.append("\r\n");
//
//        for (int i = 0; i < matrix.length; i++) {
//            printBuilder.append(format(nodesArray[i]));
//            for (int j = 0; j < matrix[0].length; j++) {
//                printBuilder.append(format(matrix[i][j]));
//                fileBuilder.append(matrix[i][j]);
//                if (j != matrix[0].length - 1) {
//                    fileBuilder.append(';');
//                }
//            }
//            printBuilder.append("\n");
//            fileBuilder.append("\r\n");
//        }
//        System.out.println(printBuilder);
//        System.out.println("\n\n" + fileBuilder);
//        fileText = fileBuilder.toString();
//    }
//
//    private String format(String text) {
//        return String.format("%1$8s", text);
//    }
//
//    private String format(Object o) {
//        return String.format("%1$8s", o);
//    }
//
//    public void clearAll() {
//        for (Node node : nodes) {
//            node.disconnectAll();
//        }
//        beginNode = null;
//        endNode = null;
//
//        nodes.clear();
//        nodesToCells.clear();
//        portsConnections.clear();
//        matrix = null;
//
//        graph.removeCells(graph.getChildCells(parent));
//
//        stateController.clear();
//    }
//
//
//    public interface Action {
//        public void performAction(MouseEvent e, Object dialogResult);
//
//        public static class AddSimpleNodeAction implements Action {
//            @Override
//            public void performAction(MouseEvent e, Object dialogResult) {
//                getInstance().addNode(new Node(Node.NodeType.Simple, (String) dialogResult), e.getX(), e.getY());
//            }
//        }
//
//        public static class AddConditionalNodeAction implements Action {
//            @Override
//            public void performAction(MouseEvent e, Object dialogResult) {
//                getInstance().addNode(new Node(Node.NodeType.Condition, (String) dialogResult), e.getX(), e.getY());
//            }
//        }
//
//        public static class AddBeginNodeAction implements Action {
//            @Override
//            public void performAction(MouseEvent e, Object dialogResult) {
//                getInstance().addBegin(e.getX(), e.getY());
//            }
//        }
//
//        public static class AddEndNodeAction implements Action {
//            @Override
//            public void performAction(MouseEvent e, Object dialogResult) {
//                getInstance().addEnd(e.getX(), e.getY());
//            }
//        }
//
//        public static class DeleteNodeAction implements Action {
//            @Override
//            public void performAction(MouseEvent e, Object dialogResult) {
//                Integer result = (Integer) dialogResult;
//                if (result == 0) {
//                    getInstance().delete(e.getX(), e.getY());
//                }
//            }
//        }
//
//        public class DeleteEdgeAction implements Action {
//            @Override
//            public void performAction(MouseEvent e, Object dialogResult) {
//                mxCell edge = (mxCell) getInstance().graphComponent.getCellAt(e.getX(), e.getY());
//                if (edge.getSource() != null && edge.getTarget() != null) {
//                    Node sourceNode = (Node) edge.getSource().getParent().getValue();
//                    Node targetNode = (Node) edge.getTarget().getParent().getValue();
//                    sourceNode.disconnectFrom(targetNode);
//
//                    if(!getInstance().isTargeted(targetNode)){
//                        Port inPort = targetNode.getInputPort();
//                        getInstance().stateController.remove(inPort.getState());
//                        getInstance().displayPortAsState(inPort.getCell(), false);
//                    }
//
//                    portsConnections.remove(new Connection<>(edge.getSource(), edge.getTarget()));
//                }
//                graph.getModel().remove(edge);
//            }
//        }
//    }
//
//    public static enum PopupAction {
//        AddSimple(
//                "Додати вершину",
//                true,
//                new Dialog.InputStringDialog("Створення нової вершини", "Введіть операції в вершині"),
//                new Action.AddSimpleNodeAction()
//        ),
//        AddCondition(
//                "Додати умову",
//                true,
//                new Dialog.InputStringDialog("Створення умовної вершини", "Введіть умову в вершині"),
//                new Action.AddConditionalNodeAction()
//        ),
//        AddBegin(
//                "Додати \"Початок\"",
//                false,
//                null,
//                new Action.AddBeginNodeAction()
//        ),
//        AddEnd(
//                "Додати \"Кінець\"",
//                false,
//                null,
//                new Action.AddEndNodeAction()
//        ),
//        DeleteNode(
//                "Видалити вершину",
//                true,
//                new Dialog.ConfirmationDialog("Ви впевнені, що бажаєте видалити цю вершину?"),
//                new Action.DeleteNodeAction()
//        ),
//        DeleteEdge(
//                "Видалити зв'язок",
//                false,
////                new Dialog.ConfirmationDialog("Ви впевнені, що бажаєте видалити цей зв'язок?"),
//                null,
//                new Action.DeleteEdgeAction()
//        );
//
//        public String text;
//        public boolean needsDialog;
//        public Dialog dialog;
//        public Action action;
//
//        PopupAction(String text, boolean needsDialog, Dialog dialog, Action action) {
//            this.text = text;
//            this.needsDialog = needsDialog;
//            this.dialog = dialog;
//            this.action = action;
//        }
//    }
//
//    public PopupAction[] getPopupActions(MouseEvent e) {
//        Object o = graphComponent.getCellAt(e.getX(), e.getY());
//        if (o != null) {
//            if (o instanceof mxCell) {
//                mxCell cell = (mxCell) o;
//                if (!cell.isEdge()) {
//                    return new PopupAction[]{PopupAction.DeleteNode};
//                } else {
//                    return new PopupAction[]{PopupAction.DeleteEdge};
//                }
//            } else {
//                return null;
//            }
//        } else {
//            return new PopupAction[]{PopupAction.AddSimple, PopupAction.AddCondition, PopupAction.AddBegin, PopupAction.AddEnd};
//        }
//    }
//
//    public void openFile(File file) {
//        if (file != null) {
//            BufferedReader bufferedReader = null;
//            try {
//                bufferedReader = new BufferedReader(new FileReader(file));
//
//                String line = bufferedReader.readLine();
//                if (line != null) {
//                    String[] cells = line.split(";");
//                    List<mxCell> cellList = new ArrayList<>(cells.length);
//
//                    String[] cellInfo = cells[0].split(":");
//                    String[] cellCoords = cellInfo[1].split(",");
//                    beginNode = new Node(Node.NodeType.Begin, cellInfo[0]);
//                    mxCell beginCell = addNode(beginNode, Integer.parseInt(cellCoords[0]), Integer.parseInt(cellCoords[1]));
//                    cellList.add(beginCell);
//
//                    mxCell tempCell = null;
//                    Node tempNode = null;
//                    for (int i = 1; i < cells.length; i++) {
//                        cellInfo = cells[i].split(":");
//                        cellCoords = cellInfo[1].split(",");
//
//                        if (cellInfo[0].startsWith("-")) {
//                            tempNode = new Node(Node.NodeType.Condition, cellInfo[0].substring(1));
//                        } else if (cellInfo[0].startsWith("~")) {
//                            tempNode = new Node(Node.NodeType.End, cellInfo[0].substring(1));
//                            endNode = tempNode;
//                        } else {
//                            tempNode = new Node(Node.NodeType.Simple, cellInfo[0]);
//                        }
//                        tempCell = addNode(tempNode, Integer.parseInt(cellCoords[0]), Integer.parseInt(cellCoords[1]));
//                        cellList.add(tempCell);
//                    }
//
//                    String[][] transfers = new String[cells.length][cells.length];
//                    for (int i = 0; i < cells.length; i++) {
//                        transfers[i] = bufferedReader.readLine().split(";");
//                    }
//
//                    mxCell cellToConnect = null;
//                    for (int i = 0; i < transfers.length; i++) {
//                        for (int j = 0; j < transfers[i].length; j++) {
//                            String t = transfers[i][j];
//                            if (!t.equals(".")) {
//                                tempCell = cellList.get(i);
//                                tempNode = (Node) tempCell.getValue();
//                                cellToConnect = cellList.get(j);
//
//                                if (tempNode.nodeType != Node.NodeType.Condition) {
//                                    if (t.equals("-")) {
//                                        connectCells(tempCell, cellToConnect);
//                                    }
//                                } else {
//                                    connectCells(tempCell, cellToConnect, t);
//                                }
//                            }
//                        }
//                    }
//                }
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void connectCells(mxCell source, mxCell target) {
//        mxICell sourcePort = source.getChildAt(source.getChildCount() - 1);
//        mxICell targetPort = target.getChildAt(0);
//
//        graph.insertEdge(parent, null, "", sourcePort, targetPort);
//    }
//
//    private void connectCells(mxCell conditional, mxCell target, String transfer) {
//        mxICell sourcePort = transfer.equals("0") ? conditional.getChildAt(1) : conditional.getChildAt(2);
//        mxICell targetPort = target.getChildAt(0);
//
//        graph.insertEdge(parent, null, "", sourcePort, targetPort);
//    }
//
//    public void saveToFile(String filePath) {
//        if (filePath != null && !filePath.equals("")) {
//            if (fileText != null && !fileText.equals("")) {
//                BufferedWriter writer = null;
//                try {
//                    writer = new BufferedWriter(new FileWriter(filePath));
//                    writer.write(fileText);
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    try {
//                        if (writer != null)
//                            writer.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }
//}