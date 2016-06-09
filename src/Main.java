import edu.kpi.nesteruk.pzcs.model.queuing.QueueConstructorFactory;
import edu.kpi.nesteruk.pzcs.planning.planner.SingleTaskHostSearcherFactory;
import edu.kpi.nesteruk.pzcs.view.dashboard.DashboardView;
import edu.kpi.nesteruk.pzcs.view.localization.Localization;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        QueueConstructorFactory.setLabs234Variants(1, 3, 11);
        SingleTaskHostSearcherFactory.setLabs67Variants(3, 5);

        /*
    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        Localization.getInstance().init(Localization.Language.UA);
        for (UIManager.LookAndFeelInfo lookAndFeelInfo : UIManager.getInstalledLookAndFeels()) {
            if ("Windows".equals(lookAndFeelInfo.getName())) {
                UIManager.setLookAndFeel(lookAndFeelInfo.getClassName());
                break;
            }
        }

        GraphStyle.DEFAULT_STYLE = GraphStyle.Alternative;
        Task.TO_STRING_FORMAT = "T[%s]: %s";
        Processor.TO_STRING_FORMAT = "P[%s]=%s";

        GraphType.queueConstructors = Arrays.asList(
                new CriticalPathByTimeAndNumberOfNodes2<>(),
                new CriticalPathByNumberOfNodesAndCoherence10<>(),
                new CriticalPathByWeightOfNodes14()
        );

        new DashboardView(GraphType.values());
        */
        DashboardView.defaultStart();
    }
}
