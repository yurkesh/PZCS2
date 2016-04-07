package edu.kpi.nesteruk.pzcs.view.dashboard;

import edu.kpi.nesteruk.pzcs.model.queuing.primitives.CriticalNode;
import edu.kpi.nesteruk.pzcs.model.tasks.Task;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Created by Anatolii Bed on 07.04.2016.
 */
public class TableView {
    public static void showTable(String title, List<CriticalNode<Task>> queues) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Object rowData[][] = new Object[queues.size()][3];
        for (int i = 0; i < rowData.length; i++) {
            rowData[i][0] = queues.get(i).node.getId();
            rowData[i][1] = queues.get(i).node.getWeight();
            rowData[i][2] = queues.get(i).value;
        }

        Object columnNames[] = {"Вершина", "Вага", "Критерій"};
        JTable table = new JTable(rowData, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setSize(800, 480);
        frame.setVisible(true);
    }
}
