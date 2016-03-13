import edu.kpi.nesteruk.pzcs.common.GraphType;
import edu.kpi.nesteruk.pzcs.view.dashboard.DashboardView;

public class Main {

    public static void main(String[] args) {
        new DashboardView(GraphType.values());
    }
}
