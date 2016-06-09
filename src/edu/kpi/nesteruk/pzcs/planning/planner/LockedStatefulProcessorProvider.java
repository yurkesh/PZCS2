package edu.kpi.nesteruk.pzcs.planning.planner;

import edu.kpi.nesteruk.pzcs.planning.processors.StatefulProcessor;

/**
 * Created by Anatolii Bed on 2016-05-25.
 */
@FunctionalInterface
interface LockedStatefulProcessorProvider {

    /**
     *
     * @param processorID to get its stateful implementation
     * @return copy existing StatefulProcessor, NOT the original one
     */
    StatefulProcessor getLockedStatefulProcessor(String processorID);

}
