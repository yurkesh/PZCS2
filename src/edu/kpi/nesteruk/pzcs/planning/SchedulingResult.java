package edu.kpi.nesteruk.pzcs.planning;

import edu.kpi.nesteruk.misc.Tuple;
import edu.kpi.nesteruk.pzcs.planning.processors.ScheduledJobsHolder;
import edu.kpi.nesteruk.pzcs.view.print.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * Created by Yurii on 2016-06-02.
 */
public class SchedulingResult {

    public final int tactsNumber;
    public final Map<String, ? extends ScheduledJobsHolder> jobHoldersMap;

    public SchedulingResult(int tactsNumber, Map<String, ? extends ScheduledJobsHolder> jobHoldersMap) {
        this.tactsNumber = tactsNumber;
        this.jobHoldersMap = Collections.unmodifiableMap(jobHoldersMap);
    }

    public int getTactsNumber() {
        return tactsNumber;
    }

    public Tuple<Table> getExecutionAndTransfersTables() {
        return Tuple.of(makeExecutionTable(tactsNumber), makeTransfersTable(tactsNumber));
    }

    private Table makeExecutionTable(int tacts) {
        int numberOfProcessors = jobHoldersMap.size();
        int numberOfColumns = tacts + 1;

        return new Table() {
            @Override
            public String[] getColumnsNames() {
                String[] columns = new String[numberOfColumns];
                columns[0] = "#";
                for (int i = 1; i <= tacts; i++) {
                    columns[i] = String.valueOf(i - 1);
                }
                return columns;
            }

            @Override
            public String[][] getColumnsData() {
                ArrayList<String> keys = new ArrayList<>(jobHoldersMap.keySet());
                String[][] data = new String[numberOfProcessors][];
                String previousTask = null;
                for (int processor = 0; processor < numberOfProcessors; processor++) {
                    data[processor] = new String[numberOfColumns];
                    String processorId = keys.get(processor);
                    data[processor][0] = processorId;

                    ScheduledJobsHolder jobsHolder = jobHoldersMap.get(processorId);
                    String task;
                    for (int tact = 0; tact < tacts; tact++) {
                        task = jobsHolder.getExecutingTask(tact);
                        String cell = task == null ? "" : (task.equals(previousTask) ? task : "*" + task);
                        previousTask = task;
                        data[processor][tact + 1] = cell;
                    }
                }
                return data;
            }
        };
    }

    private Table makeTransfersTable(int tacts) {
        int processors = jobHoldersMap.size();
        int channels = jobHoldersMap.values().stream().mapToInt(ScheduledJobsHolder::getNumberOfChannels).max().getAsInt();
        int numberOfColumns = tacts + 1;

        return new Table() {
            @Override
            public String[] getColumnsNames() {
                String[] columns = new String[numberOfColumns];
                columns[0] = "#";
                for (int i = 1; i <= tacts; i++) {
                    columns[i] = String.valueOf(i - 1);
                }
                return columns;
            }

            @Override
            public String[][] getColumnsData() {
                ArrayList<String> keys = new ArrayList<>(jobHoldersMap.keySet());
                String[][] data = new String[processors * channels][];
                for (int processor = 0; processor < processors; processor++) {
                    String processorId = keys.get(processor);
                    for (int channel = 0; channel < channels; channel++) {
                        int globalChannel = processor * channels + channel;
                        data[globalChannel] = new String[numberOfColumns];
                        data[globalChannel][0] = processorId + "/" + channel;

                        ScheduledJobsHolder jobsHolder = jobHoldersMap.get(processorId);
                        String transfer;
                        for (int tact = 0; tact < tacts; tact++) {
                            transfer = jobsHolder.getTransfer(channel, tact);
                            data[globalChannel][tact + 1] = transfer;
                        }
                    }
                }
                return data;
            }
        };
    }
}
