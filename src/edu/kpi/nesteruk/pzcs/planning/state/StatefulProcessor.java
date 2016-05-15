package edu.kpi.nesteruk.pzcs.planning.state;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.model.tasks.Task;
import edu.kpi.nesteruk.pzcs.planning.transfering.Parcel;

/**
 * Created by Yurii on 2016-05-14.
 */
public interface StatefulProcessor {

    boolean isFree(int tact);
    int getExecutionTime(int taskWeight);
    void addTask(int tact, Task task);
    void addMessage(int tact, Parcel parcel);
    Pair<Task, Parcel> getFinished(int tact);

    /*
    State getState();
    enum State {
        Free,
        Busy,
        Releasing
    }
    */
}
