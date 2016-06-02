package edu.kpi.nesteruk.pzcs.planning;

import edu.kpi.nesteruk.pzcs.model.system.ProcessorsGraph;

import java.util.List;

/**
 * Created by Anatolii Bed on 2016-06-02.
 */
@FunctionalInterface
public interface ProcessorsToScheduleOnSorter {

    List<String> sort(ProcessorsGraph processorsGraph);
}
