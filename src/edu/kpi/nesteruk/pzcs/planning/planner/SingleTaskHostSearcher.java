package edu.kpi.nesteruk.pzcs.planning.planner;

import edu.kpi.nesteruk.pzcs.planning.state.StatefulProcessor;
import edu.kpi.nesteruk.pzcs.planning.tasks.TaskWithHostedDependencies;

/**
 * Created by Yurii on 2016-05-22.
 */
@FunctionalInterface
public interface SingleTaskHostSearcher {

    int getStartTime(
            TaskTransferRouter router,
            int tact,
            TaskWithHostedDependencies task,
            StatefulProcessor processor,
            TaskFinishTimeProvider taskFinishTimeProvider,
            LockedStatefulProcessorProvider processorProvider);

}
