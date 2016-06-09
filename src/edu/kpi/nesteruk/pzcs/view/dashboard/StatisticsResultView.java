package edu.kpi.nesteruk.pzcs.view.dashboard;

import edu.kpi.nesteruk.pzcs.model.queuing.primitives.CriticalNode;
import edu.kpi.nesteruk.pzcs.model.tasks.Task;
import edu.kpi.nesteruk.pzcs.view.print.Table;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Created by Anatolii Bed on 09.06.2016.
 */
public class StatisticsResultView {
    public static void showStatistics(String title, Table table) {

        String[] columnsNames = table.getColumnsNames();
        String[][] columnsData = table.getColumnsData();

        int N = columnsData.length / 6;
        for (int i = 0; i < N; i++) {
            String[][] strings = new String[N][columnsData[0].length];
            for (int j = 0; j < N; j++) {
                for (int k = 0; k < columnsData[0].length; k++) {
                    strings[j][k] = columnsData[j * N][k];
                }
            }
            JFrame frame = new JFrame(title + " " + N);
            JTable jTable = new JTable(strings, columnsNames);
            JScrollPane scrollPane = new JScrollPane(jTable);
            frame.add(scrollPane, BorderLayout.CENTER);
            frame.setSize(800, 480);
            frame.setVisible(true);
        }
    }
}
