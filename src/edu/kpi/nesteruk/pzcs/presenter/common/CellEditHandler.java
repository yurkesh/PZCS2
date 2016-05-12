package edu.kpi.nesteruk.pzcs.presenter.common;

import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxEventSource;

import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * Created by Anatolii Bed on 2016-05-12.
 */
public class CellEditHandler {

    private final CellEditChecker editChecker;
    private final CellEditApplier cellEditApplier;

    public CellEditHandler(CellEditChecker editChecker, CellEditApplier editApplier) {
        this.editChecker = editChecker;
        this.cellEditApplier = editApplier;
    }

    private mxICell currentCell;
    private final mxEventSource.mxIEventListener labelStartEditingListener = (sender, evt) -> {
        mxICell cell = (mxICell) evt.getProperties().get("cell");
        if(CellEditHandler.this.editChecker.test(cell)) {
            currentCell = cell;
        }
    };

    private final mxEventSource.mxIEventListener labelChangedListener = (sender, evt) -> {
        if(currentCell != null) {
            mxICell editedCell = (mxICell) evt.getProperties().get("cell");
            if(currentCell.equals(editedCell)) {
                String edgeId = editedCell.getId();
                String text = (String) evt.getProperties().get("value");
                String value = CellEditHandler.this.cellEditApplier.apply(edgeId, text);
                editedCell.setValue(value);
            } else {
                throw new IllegalStateException("Edit started on another cell. StartCell = " + currentCell + ", EndCell = " + editedCell);
            }
            currentCell = null;
        }
    };

    public mxEventSource.mxIEventListener getLabelStartEditingListener() {
        return labelStartEditingListener;
    }

    public mxEventSource.mxIEventListener getLabelChangedListener() {
        return labelChangedListener;
    }

    public interface CellEditChecker extends Predicate<mxICell> {
        /**
         *
         * @param cell to edit
         * @return true if cell can be edited
         */
        @Override
        boolean test(mxICell cell);
    }

    public interface CellEditApplier extends BiFunction<String, String, String> {
        /**
         *
         * @param id UI id of edited cell
         * @param input user input value
         * @return new UI value
         */
        @Override
        String apply(String id, String input);
    }

}
