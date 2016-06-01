package edu.kpi.nesteruk.pzcs.view.print;

/**
 * Created by Yurii on 2015-09-17.
 */
public class MatrixTable implements Table {

    private final int[][] matrix;

    private final int width;
    private final int height;

    private String[] names;
    private String[][] columnsData;

    private CellValueInterceptor interceptor;

    public MatrixTable(int[][] matrix) {
        this.matrix = matrix;
        height = matrix.length;
        width = matrix[0].length;
    }

    public MatrixTable setCellInterceptor(CellValueInterceptor interceptor) {
        this.interceptor = interceptor;
        return this;
    }

    @Override
    public String[] getColumnsNames() {
        if(names == null) {
            buildNames();
        }
        return names;
    }

    private void buildNames() {
        names = new String[width + 1];
        names[0] = "|";
        for (int i = 0; i < width; i++) {
            names[i + 1] = String.valueOf(i);
        }
    }

    @Override
    public String[][] getColumnsData() {
        if(columnsData == null) {
            buildColumnsData();
        }
        return columnsData;
    }

    private void buildColumnsData() {
        columnsData = new String[height][];
        String[] row;
        for (int i = 0; i < height; i++) {
            row = new String[width + 1];
            row[0] = String.valueOf(i) + "|";
            for (int j = 0; j < width; j++) {
                row[j + 1] = getCellStringValue(matrix[i][j]);
            }
            columnsData[i] = row;
        }
    }

    private String getCellStringValue(int cellValue) {
        return interceptor == null ? String.valueOf(cellValue) : interceptor.intercept(cellValue);
    }

    public interface CellValueInterceptor {
        String intercept(int cellValue);
    }
}
