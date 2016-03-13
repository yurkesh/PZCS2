package edu.kpi.nesteruk.pzcs.view.dashboard;

import edu.kpi.nesteruk.pzcs.common.GraphType;
import edu.kpi.nesteruk.pzcs.view.Views;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Yurii on 09.09.2014.
 */
public class DashboardView extends JFrame {

    private final UnitedGraphsView graphsContainerView;

    public DashboardView(GraphType... graphTypes) throws HeadlessException {
        super("PZCS-2 Editor");

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(Views.DEFAULT_WINDOW_WIDTH, Views.DEFAULT_WINDOW_HEIGHT);
        setLocation(0, Views.WINDOW_MARGIN_TOP);

        graphsContainerView = new UnitedGraphsView(graphTypes);
        setContentPane(graphsContainerView.getGraphsContainer());
        
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
                    graphEditor.addActionListener(graphsContainerView::onNewGraphEditor);
                    newGraph.add(graphEditor);

                    JMenuItem graphGenerator = new JMenuItem("Graph generator");
                    graphGenerator.addActionListener(graphsContainerView::onNewGraphGenerator);
                    newGraph.add(graphGenerator);
                }

                JMenuItem openGraph = new JMenuItem("Open");
                openGraph.addActionListener(graphsContainerView::onOpenGraph);
                taskGraph.add(openGraph);

                JMenuItem saveGraph = new JMenuItem("Save");
                saveGraph.addActionListener(graphsContainerView::onSaveGraph);
                taskGraph.add(saveGraph);
            }
        }

        {
            JMenu systemGraph = new JMenu("System graph");
            menuBar.add(systemGraph);
            {
                JMenuItem newSystem = new JMenuItem("New system");
                newSystem.addActionListener(graphsContainerView::onNewSystem);
                systemGraph.add(newSystem);

                JMenuItem openSystem = new JMenuItem("Open");
                openSystem.addActionListener(graphsContainerView::onOpenSystem);
                systemGraph.add(openSystem);

                JMenuItem saveSystem = new JMenuItem("Save");
                saveSystem.addActionListener(graphsContainerView::onSaveSystem);
                systemGraph.add(saveSystem);
            }
        }

        {
            JMenu modeling = new JMenu("Modeling");
            menuBar.add(modeling);
            {
                JMenuItem processorsParams = new JMenuItem("Processors params");
                processorsParams.addActionListener(graphsContainerView::onProcessorsParams);
                modeling.add(processorsParams);

                JMenuItem gantDiagram = new JMenuItem("Gant diagram");
                gantDiagram.addActionListener(graphsContainerView::onGantDiagram);
                modeling.add(gantDiagram);
            }
        }

        {
            JMenu statistics = new JMenu("Statistics");
            statistics.addActionListener(graphsContainerView::onStatistics);
            menuBar.add(statistics);
        }

        {
            JMenu help = new JMenu("Help");
            menuBar.add(help);
            {
                JMenuItem about = new JMenuItem("About");
                about.addActionListener(graphsContainerView::onAbout);
                help.add(about);
            }
        }

        {
            JMenuItem exit = new JMenuItem("Exit");
            exit.addActionListener(graphsContainerView::onExit);
            menuBar.add(exit);
        }
    }
}
