package edu.kpi.nesteruk.pzcs.planning.state;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.planning.transfering.Parcel;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by Yurii on 2016-05-14.
 */
public interface StatefulProcessor {

    String getProcessorId();

    StatefulProcessor copy();

    int getFreeTime();

    boolean isFree(int tact);

    boolean hasTask(String task);

    boolean hasAllTasks(Collection<String> tasks);

    void assignTask(int tact, String task, int weight);

    Optional<String> getDoneTask(int tact);

    void addIdleExecution(int tact);

    void addIdleTransfer(int tact);

    List<Pair<String, Parcel>> getScheduledWork();


    /*
    int getExecutionTime(int taskWeight);
    void addTask(int tact, Task task);
    void addMessage(int tact, Parcel parcel);
    Pair<Task, Parcel> getFinished(int tact);

    int getStartTime(TaskWithHostedDependencies taskWithHostedPredecessors);
    */

    /*
    State getState();
    enum State {
        Free,
        Busy,
        Releasing
    }
    */
}
