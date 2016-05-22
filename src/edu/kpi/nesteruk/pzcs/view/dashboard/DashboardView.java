package edu.kpi.nesteruk.pzcs.view.dashboard;

import edu.kpi.nesteruk.pzcs.common.GraphType;
import edu.kpi.nesteruk.pzcs.presenter.common.DefaultPresenter;
import edu.kpi.nesteruk.pzcs.presenter.common.UnitedGraphPresenter;
import edu.kpi.nesteruk.pzcs.view.Views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Created by Yurii on 09.09.2014.
 */
public class DashboardView extends JFrame {

    private final UnitedGraphPresenter graphPresenter;

    public DashboardView(GraphType... graphTypes) throws HeadlessException {
        super("PZCS-2 Editor");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(Views.DEFAULT_WINDOW_WIDTH, Views.DEFAULT_WINDOW_HEIGHT);
        setLocation(Views.WINDOW_MARGIN_LEFT, Views.WINDOW_MARGIN_TOP);

        graphPresenter = UnitedGraphPresenter.createDefault(graphTypes);
        setContentPane(graphPresenter.getGraphsContainer());
        
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
                    graphEditor.addActionListener(graphPresenter::onNewGraphEditor);
                    newGraph.add(graphEditor);

                    JMenuItem graphGenerator = new JMenuItem("Graph generator");
                    graphGenerator.addActionListener(graphPresenter::onNewGraphGenerator);
                    newGraph.add(graphGenerator);
                }

                JMenuItem openGraph = new JMenuItem("Open");
                openGraph.addActionListener(graphPresenter::onOpenGraph);
                taskGraph.add(openGraph);

                JMenuItem saveGraph = new JMenuItem("Save");
                saveGraph.addActionListener(graphPresenter::onSaveGraph);
                taskGraph.add(saveGraph);

                JMenuItem makeQueues = new JMenuItem("Make queues");
                makeQueues.addActionListener(graphPresenter::onMakeQueues);
                taskGraph.add(makeQueues);
            }
        }

        {
            JMenu systemGraph = new JMenu("System graph");
            menuBar.add(systemGraph);
            {
                JMenuItem newSystem = new JMenuItem("New system");
                newSystem.addActionListener(graphPresenter::onNewSystem);
                systemGraph.add(newSystem);

                JMenuItem openSystem = new JMenuItem("Open");
                openSystem.addActionListener(graphPresenter::onOpenSystem);
                systemGraph.add(openSystem);

                JMenuItem saveSystem = new JMenuItem("Save");
                saveSystem.addActionListener(graphPresenter::onSaveSystem);
                systemGraph.add(saveSystem);
            }
        }

        {
            JMenu modeling = new JMenu("Modeling");
            menuBar.add(modeling);
            {
                JMenuItem processorsParams = new JMenuItem("Processors params");
                processorsParams.addActionListener(graphPresenter::onProcessorsParams);
                modeling.add(processorsParams);

                JMenuItem gantDiagram = new JMenuItem("Gant diagram");
                gantDiagram.addActionListener(graphPresenter::onGantDiagram);
                modeling.add(gantDiagram);
            }
        }

        {
            JMenu statistics = new JMenu("Statistics");
            statistics.addActionListener(graphPresenter::onStatistics);
            menuBar.add(statistics);
        }

        {
            JMenu help = new JMenu("Help");
            menuBar.add(help);
            {
                JMenuItem about = new JMenuItem("About");
                about.addActionListener(graphPresenter::onAbout);
                help.add(about);
            }
        }

        {
            JMenuItem validate = new JMenuItem("Validate");
            validate.addActionListener(graphPresenter::onValidate);
            menuBar.add(validate);
        }

        {
            JMenuItem exit = new JMenuItem("Exit");
            exit.addActionListener(graphPresenter::onExit);
            menuBar.add(exit);
        }
    }

    public static DashboardView defaultStart() {
        return new DashboardView(GraphType.values());
    }

    public UnitedGraphPresenter getGraphPresenter() {
        return graphPresenter;
    }
}
