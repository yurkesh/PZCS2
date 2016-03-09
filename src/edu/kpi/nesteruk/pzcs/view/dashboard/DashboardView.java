package edu.kpi.nesteruk.pzcs.view.dashboard;

import com.mxgraph.swing.mxGraphComponent;
import edu.kpi.nesteruk.pzcs.view.Views;
import edu.kpi.nesteruk.util.CollectionUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;

/**
 * Created by Yurii on 09.09.2014.
 */
public class DashboardView extends JFrame {

    private mxGraphComponent graphComponent;

    private DashboardPresenter presenter;

    private final JPanel mainPanel;

    public DashboardView() throws HeadlessException {
        super("Редактор блок-схем алгоритмів");

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(Views.DEFAULT_WINDOW_WIDTH, Views.DEFAULT_WINDOW_HEIGHT);
        setLocation(0, Views.WINDOW_MARGIN_TOP);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        presenter = new DashboardPresenter(this);

        JMenuBar menuBar = new JMenuBar();
        initHeaderMenu(menuBar);
        setJMenuBar(menuBar);

        setVisible(true);
    }

    private void initHeaderMenu(JMenuBar menuBar) {
        {
            JMenu taskGraph = new JMenu("Task graph");
            menuBar.add(taskGraph);
            {
                JMenu newGraph = new JMenu("New");
                taskGraph.add(newGraph);
                {
                    JMenuItem graphEditor = new JMenuItem("Graph editor");
                    graphEditor.addActionListener(presenter::onNewGraphEditor);
                    newGraph.add(graphEditor);

                    JMenuItem graphGenerator = new JMenuItem("Graph generator");
                    graphGenerator.addActionListener(presenter::onNewGraphGenerator);
                    newGraph.add(graphGenerator);
                }

                JMenuItem openGraph = new JMenuItem("Open");
                openGraph.addActionListener(presenter::onOpenGraph);
                taskGraph.add(openGraph);

                JMenuItem saveGraph = new JMenuItem("Save");
                saveGraph.addActionListener(presenter::onSaveGraph);
                taskGraph.add(saveGraph);
            }
        }

        {
            JMenu systemGraph = new JMenu("System graph");
            menuBar.add(systemGraph);
            {
                JMenuItem newSystem = new JMenuItem("New system");
                newSystem.addActionListener(presenter::onNewSystem);
                systemGraph.add(newSystem);

                JMenuItem openSystem = new JMenuItem("Open");
                openSystem.addActionListener(presenter::onOpenSystem);
                systemGraph.add(openSystem);

                JMenuItem saveSystem = new JMenuItem("Save");
                saveSystem.addActionListener(presenter::onSaveSystem);
                systemGraph.add(saveSystem);
            }
        }

        {
            JMenu modeling = new JMenu("Modeling");
            menuBar.add(modeling);
            {
                JMenuItem processorsParams = new JMenuItem("Processors params");
                processorsParams.addActionListener(presenter::onProcessorsParams);
                modeling.add(processorsParams);

                JMenuItem gantDiagram = new JMenuItem("Gant diagram");
                gantDiagram.addActionListener(presenter::onGantDiagram);
                modeling.add(gantDiagram);
            }
        }

        {
            JMenu statistics = new JMenu("Statistics");
            statistics.addActionListener(presenter::onStatistics);
            menuBar.add(statistics);
        }

        {
            JMenu help = new JMenu("Help");
            menuBar.add(help);
            {
                JMenuItem about = new JMenuItem("About");
                about.addActionListener(presenter::onAbout);
                help.add(about);
            }
        }

        {
            JMenuItem exit = new JMenuItem("Exit");
            exit.addActionListener(presenter::onExit);
            menuBar.add(exit);
        }
    }

    public void setGraphComponent(mxGraphComponent graphComponent) {
        this.graphComponent = graphComponent;
        graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    presenter.onDoubleClick(e);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                handleShowPopupAction(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                handleShowPopupAction(e);
            }

            private void handleShowPopupAction(final MouseEvent event) {
                if(!event.isPopupTrigger()) {
                    return;
                }

                Collection<JMenuItem> jMenuItems = presenter.getMenuItemsForClick(event);
                if(CollectionUtils.isEmpty(jMenuItems)) {
                    return;
                }

                JPopupMenu menu = new JPopupMenu("Choose action:");
                jMenuItems.forEach(menu::add);
                menu.show(graphComponent.getGraphControl(), event.getX(), event.getY());
            }
        });
        getContentPane().add(this.graphComponent, BorderLayout.CENTER);
    }
}
