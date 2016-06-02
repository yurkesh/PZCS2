package edu.kpi.nesteruk.pzcs.planning.planner;

import edu.kpi.nesteruk.misc.FunctionWithCache;
import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.model.primitives.CongenericLink;
import edu.kpi.nesteruk.pzcs.model.system.Processor;
import edu.kpi.nesteruk.pzcs.planning.processors.ChannelTransfer;
import edu.kpi.nesteruk.pzcs.planning.processors.StatefulProcessor;
import edu.kpi.nesteruk.pzcs.planning.tasks.TaskHostedDependency;
import edu.kpi.nesteruk.pzcs.planning.tasks.TaskWithHostedDependencies;

import java.util.*;
import java.util.function.Function;

/**
 * Created by Anatolii Bed on 2016-06-02.
 */
class TaskWithTransferEstimatePerformer {

    public static TaskWithTransfersEstimate getMinAvailableStartTimeOfTaskDependingOnTransfers(
            int tact,
            TaskFinishTimeProvider taskFinishTimeProvider,
            TaskWithHostedDependencies task,
            StatefulProcessor processor,
            TaskTransferRouter router,
            LockedStatefulProcessorProvider processorProvider,
            boolean transferBackwardPrediction) {

        // TODO: 2016-05-24 Add metric customization
        TaskDependencyTransferPriorityComparator taskTransferPriorityComparator =
                getTaskDependencyTransferPriorityComparator(tact, taskFinishTimeProvider, task, processor);


        final Function<String, StatefulProcessor> cachedProcessorProvider = new FunctionWithCache<>(
                processorProvider::getLockedStatefulProcessor
        );
//        LockedStatefulProcessorProvider localProcessorProvider = cachedProcessorProvider::apply;

        //Get and process all dependencies of current task that are hosted not on current processor
        List<TaskHostedDependency> nonOnThisProcessorDependencies =
                task.getAllDependenciesExcluding(processor.getId());

        Collection<TaskTransfer> taskTransfers = new ArrayList<>();

        OptionalInt maxOpt = nonOnThisProcessorDependencies.stream()
                //Sort all these dependencies tasks by their metric
                .sorted((t1, t2) -> taskTransferPriorityComparator.compare(t1.getSourceTaskId(), t2.getSourceTaskId()))
                //For all dependencies find the time of transfer arrival on this processor:
                .mapToInt(taskHostedDependency -> {
                    //Get all possible routes for this transfer.
                    List<List<CongenericLink<Processor>>> allRoutes = router.getAllRoutesBetweenProcessors(
                            taskHostedDependency.getProcessorId(),
                            processor.getId()
                    );

                    Pair<Integer, List<ProcessorTransfer>> bestEstimate = allRoutes.stream()
                            //Find the route with the best (the lowest) transfer time
                            .map(route -> getTransferTimeAndTransfers(
                                    tact,
                                    taskHostedDependency.getTransferWeight(),
                                    taskHostedDependency.getTransferId(),
                                    taskHostedDependency.getProcessorId(),
                                    route,
                                    cachedProcessorProvider
                            ))
                            .min(Comparator.comparing(Pair::getFirst))
                            .get();

                    bestEstimate.second.stream().forEach(processorTransfer -> {
                        cachedProcessorProvider.apply(processorTransfer.srcProcessor).assignTransfer(processorTransfer);
                        cachedProcessorProvider.apply(processorTransfer.destProcessor).assignTransfer(processorTransfer);
                    });

                    taskTransfers.add(
                            new TaskTransfer(
                                    taskHostedDependency.getSourceTaskId(),
                                    taskHostedDependency.getDestinationTaskId(),
                                    bestEstimate.second
                            )
                    );
                    return bestEstimate.first;
                })
                //Get max time of transfer arrival -> this is the minimum time of task start on specified processor
                // (because task cannot be started before all its dependencies transfers arrive)
                .max();

        if(!maxOpt.isPresent()) {
            throw new IllegalStateException();
        }

        int lastTransferFinished = maxOpt.getAsInt();

        int minStartTime = processor.getMinStartTime(lastTransferFinished, task.weight);

        return new TaskWithTransfersEstimate(minStartTime, taskTransfers);
    }

    private static Pair<Integer, List<ProcessorTransfer>> getTransferTimeAndTransfers(
            int tact,
            int transferWeight,
            String transfer,
            String initialSender,
            List<CongenericLink<Processor>> route,
            Function<String, StatefulProcessor> processorProvider) {

        List<ProcessorTransfer> processorTransfers = new ArrayList<>();
        Pair<Integer, String> transferPair = route.stream()
                //For all links in route:
                //Calculate the time of completion for all transfers between processors in route for specified transfer
                // between tasks
                .reduce(
                        //Start counting from specified task and specified processor
                        new Pair<>(tact, initialSender),
                        //Use previous calculated value as start for current processors pair
                        (previousTact, processorsLink) -> {
                            String senderId = previousTact.second;
                            String receiverId = processorsLink.getReceiver(senderId);
                            StatefulProcessor sender = processorProvider.apply(senderId);
                            StatefulProcessor receiver = processorProvider.apply(receiverId);
                            //Get estimate of transferring message
                            ChannelTransfer channelTransfer = sender.getSendingEstimate(previousTact.first, transferWeight, receiver);
                            //Check: we cannot use estimate that returns earlier time that we specified
                            if (channelTransfer.startTact < previousTact.first) {
                                throw new IllegalStateException();
                            }
                            channelTransfer.setTransfer(transfer);
                            processorTransfers.add(
                                    new ProcessorTransfer(senderId, receiverId, channelTransfer)
                            );
                            //Return only amount of time between specified startTact and end of receiving
                            return new Pair<>(
                                    channelTransfer.startTact + transferWeight,
                                    receiverId
                            );
                        },
                        //This will be used to combine results of parallel operations (not needed for us now)
                        (u, u2) -> {
                            throw new UnsupportedOperationException();
                        }
                );

        int transferLength = transferPair.first;

        return Pair.create(transferLength, processorTransfers);
    }

    private static TaskDependencyTransferPriorityComparator getTaskDependencyTransferPriorityComparator(
            int tact,
            TaskFinishTimeProvider finishTimeProvider,
            TaskWithHostedDependencies task,
            StatefulProcessor processor) {

        return TaskDependencyTransferPriorityComparator.getTaskTransferPriorityCalculator(
                tact,
                TaskDependencyTransferWeightProvider.getTaskDependencyTransferProvider(
                        task,
                        processor.getId()
                ),
                finishTimeProvider
        );
    }
}
