package edu.kpi.nesteruk.pzcs.view.dashboard;

import edu.kpi.nesteruk.pzcs.model.queuing.primitives.CriticalNode;
import edu.kpi.nesteruk.pzcs.model.tasks.Task;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Created by Anatolii Bed on 09.06.2016.
 */
public class StatisticsResultView {
    public static void showStatistics(String title) {
        JFrame frame = new JFrame(title);

        Object rowData[][] = new Object[3][3];
        String[] strings = {"a"};
        Object[] strings1 =  strings;
        Object columnNames[] = {"Вершина", "Вага", "Критерій"};
        JTable table = new JTable(rowData, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setSize(800, 480);
        frame.setVisible(true);
    }
}
