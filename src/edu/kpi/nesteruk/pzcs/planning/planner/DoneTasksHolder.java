package edu.kpi.nesteruk.pzcs.planning.planner;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yurii on 2016-05-23.
 */
interface DoneTasksHolder extends TaskFinishTimeProvider {
    void addDoneTasks(int tact, Collection<String> doneTasks);
    boolean hasNotDone();
    boolean hasDone();

    static DoneTasksHolder getDoneTasksHolder(Collection<String> allTasks, Collection<String> doneTasks) {
        Map<String, Integer> doneTaskToFinishTimeMap = new HashMap<>();
        return new DoneTasksHolder() {

            @Override
            public boolean hasDone() {
                return !doneTaskToFinishTimeMap.isEmpty();
            }

            @Override
            public void addDoneTasks(int tact, Collection<String> done) {
                doneTasks.addAll(done);
                done.stream().forEach(doneTask -> doneTaskToFinishTimeMap.put(doneTask, tact));
            }

            @Override
            public int getFinishTimeOfTask(String taskId) {
                return doneTaskToFinishTimeMap.get(taskId);
            }

            @Override
            public boolean hasNotDone() {
                return !doneTasks.containsAll(allTasks);
            }
        };
    }
}
