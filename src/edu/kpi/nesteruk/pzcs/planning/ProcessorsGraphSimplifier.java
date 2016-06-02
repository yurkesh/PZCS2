package edu.kpi.nesteruk.pzcs.planning;

import edu.kpi.nesteruk.pzcs.model.system.ProcessorsGraph;
import edu.kpi.nesteruk.pzcs.model.system.ProcessorsGraphBundle;

/**
 * Created by Anatolii Bed on 2016-06-02.
 */
@FunctionalInterface
public interface ProcessorsGraphSimplifier {

    ProcessorsGraph convertToGraph(ProcessorsGraphBundle processorsGraphBundle);

}
