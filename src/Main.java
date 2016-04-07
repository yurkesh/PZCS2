import edu.kpi.nesteruk.pzcs.common.GraphType;
import edu.kpi.nesteruk.pzcs.model.queuing.concrete2.CriticalPathByTimeAndNumberOfNodes2;
import edu.kpi.nesteruk.pzcs.model.system.Processor;
import edu.kpi.nesteruk.pzcs.model.tasks.Task;
import edu.kpi.nesteruk.pzcs.view.GraphStyle;
import edu.kpi.nesteruk.pzcs.view.dashboard.DashboardView;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        /*
        GraphStyle.DEFAULT_STYLE = GraphStyle.Alternative;
        Task.TO_STRING_FORMAT = "Task[%s]: %s";
        Processor.TO_STRING_FORMAT = "Processor[%s]=%s";

        GraphType.queueConstructors = Arrays.asList(
                new CriticalPathByTimeAndNumberOfNodes2<>()
        );
        */

        new DashboardView(GraphType.values());
    }
}
