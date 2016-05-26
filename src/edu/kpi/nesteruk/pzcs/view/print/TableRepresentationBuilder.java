package edu.kpi.nesteruk.pzcs.view.print;

import edu.kpi.nesteruk.util.CollectionUtils;

/**
 * Created by Yurii on 2015-09-17.
 */
public class TableRepresentationBuilder {

    public static final String DIV_ESCAPE = "\n\t\r\"\\";

    public static final char COLUMNS_DELIMITER = '|';

    private final Table table;

    private int[] columnsWidth;

    private String representation;

    public TableRepresentationBuilder(Table table) {
        this.table = table;
    }

    public String getRepresentation() {
        if(representation != null) {
            return representation;
        }

        if(columnsWidth == null) {
            clarifyColumnsWidth();
        }

        StringBuilder builder = new StringBuilder();
//        format(builder, "", columnsWidth[0]);
        String[] columnsNames = table.getColumnsNames();
        for (int i = 0; i < columnsNames.length; i++) {
            format(builder, columnsNames[i], columnsWidth[i]).append(COLUMNS_DELIMITER);
        }
        builder.append("\n");
        CollectionUtils.join(
                builder,
                table.getColumnsData(),
                (sb, row) -> {
                    for (int j = 0; j < row.length; j++) {
                        String cell = row[j];
                        if(cell != null && cell.startsWith(DIV_ESCAPE)) {
                            String div = cell.substring(DIV_ESCAPE.length());
                            cell = "";
                            while (cell.length() < columnsWidth[j]) {
                                cell += div;
                            }
                            builder.append(cell);
                        } else {
                            format(builder, cell, columnsWidth[j]);
                        }
                        builder.append('|');
                    }
                },
                "\n"
        );
        representation = builder.toString();
        return representation;
    }

    private void clarifyColumnsWidth() {
        String[] names = table.getColumnsNames();
        columnsWidth = new int[names.length];
        updateColumnsWidth(names);
        String[][] columnsData = table.getColumnsData();
        for (String[] aColumnsData : columnsData) {
            updateColumnsWidth(aColumnsData);
        }
    }

    private void updateColumnsWidth(String[] row) {
        String cell;
        int cellLength;
        for (int i = 0; i < row.length; i++) {
            cell = row[i];
            if(cell != null) {
                if(cell.startsWith(DIV_ESCAPE)) {
                    cell = cell.substring(DIV_ESCAPE.length());
                }
                cellLength = cell.length();
                if (columnsWidth[i] <= cellLength) {
                    columnsWidth[i] = cellLength + 1;
                }
            }
        }
    }

    public static StringBuilder format(StringBuilder builder, String str, int minWidth) {
        str = str == null ? "" : str;
        int padding = minWidth - str.length();
        for (int i = 0; i < padding; i++) {
            builder.append(" ");
        }
        return builder.append(str);
    }

    public static StringBuilder format(StringBuilder builder, int number, int minWidth) {
        String str = String.valueOf(number);
        int padding = minWidth - str.length();
        for (int i = 0; i < padding; i++) {
            builder.append(" ");
        }
        return builder.append(str);
    }

}
