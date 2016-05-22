package edu.kpi.nesteruk.pzcs.planning.planner;

import java.util.Collection;

/**
 * Created by Yurii on 2016-05-23.
 */
interface DoneTasksHolder {
    void addDoneTasks(Collection<String> doneTasks);
    boolean hasNotDone();

    static DoneTasksHolder getDoneTasksHolder(Collection<String> allTasks, Collection<String> doneTasks) {
        return new DoneTasksHolder() {

            @Override
            public void addDoneTasks(Collection<String> done) {
                doneTasks.addAll(done);
            }

            @Override
            public boolean hasNotDone() {
                return !doneTasks.contains(allTasks);
            }
        };
    }
}
