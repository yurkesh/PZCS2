package edu.kpi.nesteruk.pzcs.scheduling;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.view.print.Table;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private static final String[] COLUMNS = {"#", "Tasks", "Coherence", "Queue", "Planner", "SpeedUp", "System Ef", "Scheduler Ef"};

    private static final NumberFormat DOUBLE_FORMAT = new DecimalFormat("#.##");

    public static Table makeTableResult(List<Pair<ConcreteTasksJob, Map<SchedulerCase, ResultIndicators>>> results) {
        return makeTableResult(6, results);
    }

    public static Table makeTableResult(int numberOfSchedulers, List<Pair<ConcreteTasksJob, Map<SchedulerCase, ResultIndicators>>> results) {
        return new Table() {
            @Override
            public String[] getColumnsNames() {
                return COLUMNS;
            }

            @Override
            public String[][] getColumnsData() {
                return CompositePlannerTestingResultsInterpreter.getColumnsData(results);
            }
        };
    }

    private static String[][] getColumnsData(List<Pair<ConcreteTasksJob, Map<SchedulerCase, ResultIndicators>>> results) {
        Collection<RowHolder> rows = getRows(results);
        String[][] data = new String[rows.size()][];
        int counter = 0;
        for (RowHolder rowHolder : rows) {
            rowHolder.row[0] = String.valueOf(counter);
            data[counter++] = rowHolder.row;
        }
        return data;
    }

    private static Collection<RowHolder> getRows(List<Pair<ConcreteTasksJob, Map<SchedulerCase, ResultIndicators>>> results) {
        Map<Pair<JobCase, SchedulerCase>, List<ResultIndicators>> collected = results.stream()
                .map(jobAndSchedulersResultPair ->
                        jobAndSchedulersResultPair.second.entrySet().stream()
                                .map(schedulerResultEntry ->
                                        Pair.create(jobAndSchedulersResultPair.first.jobCase, schedulerResultEntry)
                                )
                )
                .flatMap(Function.identity())
                .collect(Collectors.groupingBy(
                        concreteTasksJobEntryPair -> Pair.create(concreteTasksJobEntryPair.first, concreteTasksJobEntryPair.second.getKey()),
                        Collectors.mapping(
                                concreteTasksJobEntryPair -> concreteTasksJobEntryPair.second.getValue(),
                                Collectors.toList()
                        )
                ));
        LinkedHashSet<Pair<Pair<JobCase, SchedulerCase>, ResultIndicators>> resultsSet = collected.entrySet().stream()
                .map(entry -> {
                    AverageIndicators average = entry.getValue().stream().collect(
                            AverageIndicators::new,
                            AverageIndicators::accept,
                            AverageIndicators::combine
                    );
                    return Pair.create(entry.getKey(), average.get());
                })
                .sorted(Comparator.<Pair<Pair<JobCase, SchedulerCase>, ResultIndicators>, JobCase>comparing(pair -> pair.first.first).thenComparing(pair -> pair.second))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        List<RowHolder> rows = resultsSet.stream()
                .map(pair -> {
                    JobCase jobCase = pair.first.first;
                    SchedulerCase schedulerCase = pair.first.second;
                    ResultIndicators result = pair.second;

                    String[] row = new String[COLUMNS.length];

                    //Tasks
                    row[1] = String.valueOf(jobCase.numberOfTasks);

                    //Coherence
                    row[2] = String.valueOf(DOUBLE_FORMAT.format(jobCase.tasksGraphCoherence));

                    //Queue
                    row[3] = String.valueOf(schedulerCase.queueConstructorVariant);

                    //Planner
                    row[4] = String.valueOf(schedulerCase.plannerVariant);

                    //SpeedUp
                    row[5] = String.valueOf(DOUBLE_FORMAT.format(result.getSpeedUp()));

                    //System Ef
                    row[6] = String.valueOf(DOUBLE_FORMAT.format(result.getSystemEfficiency()));

                    //Scheduler Ef
                    row[7] = String.valueOf(DOUBLE_FORMAT.format(result.getSchedulerEfficiency()));

                    return new RowHolder(row);
                })
                .collect(Collectors.toList());
        return rows;

        /*
        return results.stream().map(concreteJobResultsPair -> {
            ConcreteTasksJob concreteTasksJob = concreteJobResultsPair.first;
            JobCase jobCase = concreteTasksJob.jobCase;
            return concreteJobResultsPair.second.entrySet().stream()
                    //Compare by ResultIndicator
                    .sorted(Comparator.comparing(Map.Entry::getValue))
                    .map(schedulerResultEntry -> {
                        SchedulerCase schedulerCase = schedulerResultEntry.getKey();
                        ResultIndicators result = schedulerResultEntry.getValue();

                        String[] row = new String[COLUMNS.length];

                        //Tasks
                        row[1] = String.valueOf(jobCase.numberOfTasks);

                        //Coherence
                        row[2] = String.valueOf(DOUBLE_FORMAT.format(jobCase.tasksGraphCoherence));

                        //Queue
                        row[3] = String.valueOf(schedulerCase.queueConstructorVariant);

                        //Planner
                        row[4] = String.valueOf(schedulerCase.plannerVariant);

                        //SpeedUp
                        row[5] = String.valueOf(DOUBLE_FORMAT.format(result.getSpeedUp()));

                        //System Ef
                        row[6] = String.valueOf(DOUBLE_FORMAT.format(result.getSystemEfficiency()));

                        //Scheduler Ef
                        row[7] = String.valueOf(DOUBLE_FORMAT.format(result.getSchedulerEfficiency()));

                        return new RowHolder(row);
                    });
        }).flatMap(Function.<Stream<RowHolder>>identity()).collect(Collectors.toCollection(LinkedHashSet::new));
        */
    }

    private static class RowHolder {
        public final String[] row;

        private RowHolder(String[] row) {
            this.row = row;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            RowHolder rowHolder = (RowHolder) o;

            // Probably incorrect - comparing Object[] arrays with Arrays.equals
            return Arrays.equals(row, rowHolder.row);

        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(row);
        }
    }



}
