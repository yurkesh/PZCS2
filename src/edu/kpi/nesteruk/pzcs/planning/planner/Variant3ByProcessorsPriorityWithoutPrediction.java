package edu.kpi.nesteruk.pzcs.planning.planner;

import edu.kpi.nesteruk.pzcs.planning.processors.StatefulProcessor;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Created by Anatolii Bed on 2016-06-02.
 */
class Variant3ByProcessorsPriorityWithoutPrediction extends SortingProcessorsSingleTaskHostSearcher {

    @Override
    protected Optional<StatefulProcessor> selectProcessor(int tact, List<StatefulProcessor> processorsSorted) {
        return processorsSorted.stream()
                .filter(processor -> processor.isFree(tact))
                .findFirst();
    }

    @Override
    public String toString() {
        return "Variant3ByProcessorsPriorityWithoutPrediction";
    }
}
