package edu.kpi.nesteruk.pzcs.scheduling;

import edu.kpi.nesteruk.misc.Pair;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-06-09.
 */
public class CompositePlannerTestingResultsInterpreter {

    public static List<Pair<SchedulerCase, ResultIndicators>> interpret(Map<SchedulerCase, Map<JobCase, List<ResultIndicators>>> results) {
        return results.entrySet().stream()
                .map(schedulerEntry -> {
                    //Collect average results
                    AverageIndicators averageIndicators =
                            //All entries {JobCase -> [ResultIndicator]}
                            schedulerEntry.getValue().entrySet().stream()
                                    //Get [ResultIndicator]
                                    .map(Map.Entry::getValue)
                                    //Join stream<[ResultIndicators]> into stream<ResultIndicator>
                                    .flatMap(Collection::stream)
                                    .collect(
                                            AverageIndicators::new,
                                            AverageIndicators::accept,
                                            AverageIndicators::combine
                                    );

                    //Get average result
                    ResultIndicators indicators = averageIndicators.get();
                    // Map to {SchedulerCase, ResultIndicator}
                    return new Pair<>(schedulerEntry.getKey(), indicators);
                })
                //Sort by ResultIndicator
                .sorted(Comparator.<Pair<SchedulerCase, ResultIndicators>, ResultIndicators>comparing(Pair::getSecond).reversed())
                .collect(Collectors.toList());
    }

    private static class AverageIndicators implements Consumer<ResultIndicators>, Supplier<ResultIndicators> {
        private int total = 0;
        private Collection<ResultIndicators> indicators = new ArrayList<>();

        @Override
        public ResultIndicators get() {
            return new ResultIndicators(
                    getAverage(ResultIndicators::getSpeedUp),
                    getAverage(ResultIndicators::getSystemEfficiency),
                    getAverage(ResultIndicators::getSchedulerEfficiency)
            );
        }

        private double getAverage(Function<ResultIndicators, Double> propertyProvider) {
            Double propertySum = indicators.stream()
                    .map(propertyProvider)
                    .reduce(Double::sum)
                    .get();
            return propertySum / total;
        }

        @Override
        public void accept(ResultIndicators resultIndicators) {
            total++;
            indicators.add(resultIndicators);
        }

        public void combine(AverageIndicators other) {
            this.total += other.total;
            this.indicators.addAll(other.indicators);
        }
    }

}
