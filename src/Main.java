import edu.kpi.nesteruk.pzcs.common.GraphType;
import edu.kpi.nesteruk.pzcs.view.dashboard.DashboardView;
import edu.kpi.nesteruk.pzcs.view.localization.Localization;

import javax.swing.*;

public class Main {

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        Localization.getInstance().init(Localization.Language.UA);
        for (UIManager.LookAndFeelInfo lookAndFeelInfo : UIManager.getInstalledLookAndFeels()) {
            if ("Windows".equals(lookAndFeelInfo.getName())) {
                UIManager.setLookAndFeel(lookAndFeelInfo.getClassName());
                break;
            }
        }
        new DashboardView(GraphType.values());
    }
}
