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

        JFrame frame = new JFrame(title);
        JTable jTable = new JTable(columnsData, columnsNames);
        JScrollPane scrollPane = new JScrollPane(jTable);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setSize(800, 480);
        frame.setVisible(true);

        if (true) return;
/*
        int N = columnsData.length / 6;
        System.out.println(columnsData.length);
        for (int i = 0; i < N; i++) {
            String[][] strings = new String[N][columnsData[0].length];
            for (int j = 0; j < N; j++) {
                for (int k = 0; k < columnsData[0].length; k++) {
                    System.out.println(i + " " + j + " " + k);
                    strings[j][k] = columnsData[i * N + j][k] == null ? "_" : columnsData[i * N + j][k];
                }
            }
            JFrame frame = new JFrame(title + " " + i);
            JTable jTable = new JTable(strings, columnsNames);
            JScrollPane scrollPane = new JScrollPane(jTable);
            frame.add(scrollPane, BorderLayout.CENTER);
            frame.setSize(800, 480);
            frame.setVisible(true);
        }
        */
    }
}
