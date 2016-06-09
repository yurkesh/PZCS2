import edu.kpi.nesteruk.pzcs.model.queuing.QueueConstructorFactory;
import edu.kpi.nesteruk.pzcs.planning.planner.SingleTaskHostSearcherFactory;
import edu.kpi.nesteruk.pzcs.view.dashboard.DashboardView;

public class Main {

    public static void main(String[] args) {

        QueueConstructorFactory.setLabs234Variants(1, 3, 11);
        SingleTaskHostSearcherFactory.setLabs67Variants(3, 5);

        /*
        GraphStyle.DEFAULT_STYLE = GraphStyle.Alternative;
        Task.TO_STRING_FORMAT = "Task[%s]: %s";
        Processor.TO_STRING_FORMAT = "Processor[%s]=%s";

        GraphType.queueConstructors = Arrays.asList(
                new CriticalPathByTimeAndNumberOfNodes2<>(),
                new CriticalPathByNumberOfNodesAndCoherence10<>(),
                new CriticalPathByWeightOfNodes14()
        );
        */
        /*
        new DashboardView(GraphType.values());
        */
        DashboardView.defaultStart();
    }
}
