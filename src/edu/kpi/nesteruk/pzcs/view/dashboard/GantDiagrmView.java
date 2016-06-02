package edu.kpi.nesteruk.pzcs.view.dashboard;

import edu.kpi.nesteruk.pzcs.view.print.Table;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Anatolii Bed on 02.06.2016.
 */
public class GantDiagrmView {
    public static void showDiagramForProcessors(Table table, String title) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        table = transportTable(table);
        JTable jTable = new JTable(table.getColumnsData(), table.getColumnsNames());
        JScrollPane scrollPane = new JScrollPane(jTable);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setSize(800, 480);
        frame.setVisible(true);
    }

    private static Table transportTable(Table table) {
        String[][] columnsData = table.getColumnsData();
        String[] columnsNames = table.getColumnsNames();

        String[] newColumnNames = new String[columnsData.length + 1];
        String[][] newColumnData = new String[columnsData[0].length - 1][columnsData.length + 1];

        newColumnNames[0] = "#";
        for (int i = 1; i < columnsData.length + 1; i++) {
            newColumnNames[i] = columnsData[i - 1][0];
        }

        for (int i = 0; i < newColumnData.length; i++) {
            newColumnData[i][0] = columnsNames[i + 1];
        }

        for (int i = 0; i < columnsData.length; i++) {
            for (int j = 1; j < columnsData[i].length; j++) {
                newColumnData[j - 1][i + 1] = columnsData[i][j];
            }
        }

        return new Table() {
            @Override
            public String[] getColumnsNames() {
                return newColumnNames;
            }

            @Override
            public String[][] getColumnsData() {
                return newColumnData;
            }
        };
    }
}
