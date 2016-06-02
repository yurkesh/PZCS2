package edu.kpi.nesteruk.pzcs.planning.planner;

import java.util.Comparator;

/**
 * Created by Anatolii Bed on 2016-05-24.
 */
@FunctionalInterface
interface TaskDependencyTransferPriorityComparator extends Comparator<String> {

    /**
     *
     * @param sourceTask1 task1 from which data should be sent to stored task
     * @param sourceTask2 task2 from which data should be sent to stored task
     * @return difference between calculated priorities - metric depending on time of finish, data transfer volume and
     * number of hops. Lower is better
     */
    @Override
    int compare(String sourceTask1, String sourceTask2);

    /**
     *
     * @param currentTact tact on which calculation is performed
     * @param transferWeightProvider provides weight of transfer from source task (param of comparing) to stored task
     * @param finishTimeProvider provides tack on which source task (param of comparing) was finished
     * @return comparator of source tasks relatively to stored task
     */
    static TaskDependencyTransferPriorityComparator getTaskTransferPriorityCalculator(
            int currentTact,
            TaskDependencyTransferWeightProvider transferWeightProvider,
            TaskFinishTimeProvider finishTimeProvider) {

//        Function<String, Integer> dependencyTransferPriorityCalculator = sourceTask -> {
//            int transferWeight = transferWeightProvider.getTransferWeightFromDependency(sourceTask);
//            if(transferWeight == 0) {
//                return 0;
//            }
//            int timePriority = currentTact - finishTimeProvider.getFinishTimeOfTask(sourceTask);
//
//        };

        Comparator<String> comparator = Comparator
                //First compare by weight of transfer (desc): higher weight - earlier start of transfer is needed
                .comparing(transferWeightProvider::getTransferWeightFromDependency).reversed()
                //Then compare by finish time (asc): lower time - earlier start of transfer is needed
                .thenComparing(finishTimeProvider::getFinishTimeOfTask);

        return comparator::compare;
    }

}
