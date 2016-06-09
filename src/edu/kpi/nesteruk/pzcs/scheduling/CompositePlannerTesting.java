package edu.kpi.nesteruk.pzcs.scheduling;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.common.LabWork;
import edu.kpi.nesteruk.pzcs.graph.generation.GeneratorParams;
import edu.kpi.nesteruk.pzcs.graph.generation.GraphGenerator;
import edu.kpi.nesteruk.pzcs.model.primitives.DirectedLink;
import edu.kpi.nesteruk.pzcs.model.queuing.QueueConstructorFactory;
import edu.kpi.nesteruk.pzcs.model.queuing.common.AbstractQueueConstructor;
import edu.kpi.nesteruk.pzcs.model.queuing.common.GraphPathsData;
import edu.kpi.nesteruk.pzcs.model.queuing.common.PathLengthsComputer;
import edu.kpi.nesteruk.pzcs.model.queuing.common.QueueConstructor;
import edu.kpi.nesteruk.pzcs.model.system.ProcessorsGraphBundle;
import edu.kpi.nesteruk.pzcs.model.tasks.Task;
import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraphBundle;
import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraphModel;
import edu.kpi.nesteruk.pzcs.planning.Planner;
import edu.kpi.nesteruk.pzcs.planning.SchedulingResult;
import edu.kpi.nesteruk.pzcs.planning.params.ProcessorsParams;
import edu.kpi.nesteruk.pzcs.planning.planner.NeedRetryException;
import edu.kpi.nesteruk.pzcs.planning.planner.SingleTaskHostSearcher;
import edu.kpi.nesteruk.pzcs.planning.planner.SingleTaskHostSearcherFactory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

/**
 * Created by Yurii on 2016-06-09.
 */
public class CompositePlannerTesting {

    private static final boolean LOG = true;

    private static final Random RANDOM = new Random(19);
    private static final ProcessorsParams DEFAULT_PROCESSORS_PARAMS = new ProcessorsParams(
            ProcessorsParams.NUMBER_OF_CHANNELS_BY_MAX_COHERENCE
    );

    public static List<Pair<ConcreteTasksJob, Map<SchedulerCase, ResultIndicators>>> performFullTesting(
            ProcessorsGraphBundle processorsGraphBundle,
            CompositeSchedulerTestParams params) {

        GraphGenerator<Task, DirectedLink<Task>, TasksGraphBundle> tasksGraphGenerator = TasksGraphModel.makeGenerator(RANDOM);

        GeneratorParams.Builder generatorParamsBuilder = new GeneratorParams.Builder();
        generatorParamsBuilder
                .setMinNodeWeight(params.minTaskWeight)
                .setMaxNodeWeight(params.maxTaskWeight)
                .setMinLinkWeight(params.minTasksLinkWeight)
                .setMaxLinkWeight(params.maxTasksLinkWeight);

        return runTestsForAllSchedulers(
                processorsGraphBundle,
                params,
                tasksGraphGenerator,
                generatorParamsBuilder);
    }

    private static List<Pair<ConcreteTasksJob, Map<SchedulerCase, ResultIndicators>>> runTestsForAllSchedulers(
            ProcessorsGraphBundle processorsGraphBundle,
            CompositeSchedulerTestParams params,
            GraphGenerator<Task, DirectedLink<Task>, TasksGraphBundle> tasksGraphGenerator,
            GeneratorParams.Builder generatorParamsBuilder) {

        List<LabWork> queuesTypes = Arrays.asList(LabWork.LAB_2, LabWork.LAB_3, LabWork.LAB_4);
        List<LabWork> plannersTypes = Arrays.asList(LabWork.LAB_6, LabWork.LAB_7);

        Map<SchedulerCase, Planner> plannersMap = new LinkedHashMap<>();

        QueueConstructor<Task, DirectedLink<Task>> queueConstructor;
        for (LabWork queueLabWork : queuesTypes) {
            int queueVariant = QueueConstructorFactory.getQueueConstructorVariant(queueLabWork);
            queueConstructor = QueueConstructorFactory.getByVariant(queueVariant);

            for (LabWork plannerLabWork : plannersTypes) {
                int plannerVariant = SingleTaskHostSearcherFactory.getSearcherVariant(plannerLabWork);
                SingleTaskHostSearcher searcher = SingleTaskHostSearcherFactory.getByVariant(plannerVariant);
                SchedulerCase schedulerCase = new SchedulerCase(
                        queueVariant,
                        plannerVariant
                );

                Planner planner = CommonPlannerTesting.makePlanner(
                        searcher,
                        queueConstructor
                );

                plannersMap.put(schedulerCase, planner);
            }
        }

        return runTestsForSinglePlanner(
                processorsGraphBundle,
                params,
                tasksGraphGenerator,
                generatorParamsBuilder,
                plannersMap
        );
    }

    private static List<Pair<ConcreteTasksJob, Map<SchedulerCase, ResultIndicators>>> runTestsForSinglePlanner(
            ProcessorsGraphBundle processorsGraphBundle,
            CompositeSchedulerTestParams params,
            GraphGenerator<Task, DirectedLink<Task>, TasksGraphBundle> tasksGraphGenerator,
            GeneratorParams.Builder generatorParamsBuilder,
            Map<SchedulerCase, Planner> plannersMap) {

        final int numberOfProcessors = processorsGraphBundle.getNodesMap().size();

        final List<Pair<ConcreteTasksJob, Map<SchedulerCase, ResultIndicators>>> totalResults = new CopyOnWriteArrayList<>();

        IntStream.Builder numberOfTasksBuilder = IntStream.builder();
        for (int numberOfTasks = params.minNumberOfTasks;
             numberOfTasks < params.maxNumberOfTasks;
             numberOfTasks+= params.deltaNumberOfTasks) {

            numberOfTasksBuilder.accept(numberOfTasks);
        }
        List<Integer> numbersOfTasks = numberOfTasksBuilder.build().boxed().collect(Collectors.toList());

        DoubleStream.Builder coherenceBuilder = DoubleStream.builder();
        for (double coherence = params.minTasksGraphCoherence;
             coherence <= params.maxTasksGraphCoherence;
             coherence+= params.deltaTasksGraphCoherence) {
            coherenceBuilder.accept(coherence);
        }
        List<Double> coherences = coherenceBuilder.build().boxed().collect(Collectors.toList());

        numbersOfTasks.stream().forEach(numberOfTasks -> {
            log("Number of tasks = " + numberOfTasks);

            GeneratorParams.Builder builderWithNumberOfTasks = new GeneratorParams.Builder(generatorParamsBuilder)
                    .setNumberOfNodes(numberOfTasks);

            coherences.stream().forEach(coherence -> {
                log("Coherence = " + coherence);

                GeneratorParams.Builder builderWithCoherence = new GeneratorParams.Builder(builderWithNumberOfTasks)
                        .setCoherence(coherence);

                final JobCase jobCase = new JobCase(numberOfTasks, coherence);

                GeneratorParams generatorParams = builderWithCoherence.build();

                //Parallel stream is used
                IntStream.range(0, params.numberOfTaskGraphsToGenerate).forEach(number -> {
                    log("\nJobCase = " + jobCase + "; Graph # " + number);

                    TasksGraphBundle tasksGraphBundle = tasksGraphGenerator.generate(generatorParams);
                    ConcreteTasksJob concreteTasksJob = new ConcreteTasksJob(jobCase, tasksGraphBundle);

                    Map<SchedulerCase, ResultIndicators> plannersResults = new LinkedHashMap<>();

                    for (Map.Entry<SchedulerCase, Planner> entry : plannersMap.entrySet()) {
                        SchedulerCase schedulerCase = entry.getKey();
                        Planner planner = entry.getValue();

                        SchedulingResult plannedWork = null;
                        while (plannedWork == null) {
                            try {
                                plannedWork = planner.getPlannedWork(
                                        processorsGraphBundle,
                                        tasksGraphBundle,
                                        DEFAULT_PROCESSORS_PARAMS
                                );
                            } catch (NeedRetryException e) {
//                                e.printStackTrace();
                            }
                        }
                        log("Scheduler = " + schedulerCase);

                        int singleCoreTime = getSingleCoreTime(tasksGraphBundle);
                        int theoreticCriticalTime = getTheoreticCriticalTime(tasksGraphBundle);

                        ResultIndicators indicator = ResultIndicators.calculate(
                                singleCoreTime,
                                plannedWork.getTactsNumber(),
                                numberOfProcessors,
                                theoreticCriticalTime
                        );

                        plannersResults.put(schedulerCase, indicator);
                    }

                    totalResults.add(new Pair<>(concreteTasksJob, plannersResults));
                });
            });
        });

        return totalResults;
    }

    private static int getSingleCoreTime(TasksGraphBundle tasksGraphBundle) {
        return tasksGraphBundle.getNodesMap().values().stream()
                .map(Task::getWeight)
                .reduce(Integer::sum)
                .get();
    }

    private static int getTheoreticCriticalTime(TasksGraphBundle tasksGraphBundle) {
        PathLengthsComputer<Task, DirectedLink<Task>> pathLengthsComputer = new PathLengthsComputer<>(false);
        GraphPathsData<Task> pathsData = AbstractQueueConstructor.getGraphPathsData(
                pathLengthsComputer, tasksGraphBundle
        );
        return pathsData.graphLengths.inWeight;
    }

    private static void log(Object o) {
        if(LOG) {
            System.out.println(o);
        }
    }
}
