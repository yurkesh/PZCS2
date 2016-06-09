package edu.kpi.nesteruk.pzcs.planning.planner;

import edu.kpi.nesteruk.pzcs.planning.processors.StatefulProcessor;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Created by Yurii on 2016-06-02.
 */
class Variant2TheMostIdleProcessor extends SortingProcessorsSingleTaskHostSearcher {

    @Override
    protected Optional<StatefulProcessor> selectProcessor(int tact, List<StatefulProcessor> processorsSorted) {
        //Select processor with the longest idle time
        return processorsSorted.stream()
                //Only free processors
                .filter(processor -> processor.isFree(tact))
                //Sort by time when idle
                .max(Comparator.comparing(processor -> processor.getIdleTime(tact)));
    }

    @Override
    public String toString() {
        return "Variant2TheMostIdleProcessor";
    }
}
