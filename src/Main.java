import edu.kpi.nesteruk.pzcs.common.GraphType;
import edu.kpi.nesteruk.pzcs.model.system.Processor;
import edu.kpi.nesteruk.pzcs.model.tasks.Task;
import edu.kpi.nesteruk.pzcs.view.GraphStyle;
import edu.kpi.nesteruk.pzcs.view.dashboard.DashboardView;

public class Main {

    public static void main(String[] args) {
        /*
        GraphStyle.DEFAULT_STYLE = GraphStyle.Alternative;
        Task.TO_STRING_FORMAT = "Task[%s]: %s";
        Processor.TO_STRING_FORMAT = "Processor[%s]=%s";
        */
        new DashboardView(GraphType.values());
    }
}
