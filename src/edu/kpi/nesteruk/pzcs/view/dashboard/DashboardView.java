package edu.kpi.nesteruk.pzcs.view.dashboard;

import edu.kpi.nesteruk.pzcs.common.GraphType;
import edu.kpi.nesteruk.pzcs.presenter.common.UnitedGraphPresenter;
import edu.kpi.nesteruk.pzcs.view.Views;
import edu.kpi.nesteruk.pzcs.view.localization.Localization;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Anatolii on 09.09.2014.
 */
public class DashboardView extends JFrame {

    private final UnitedGraphPresenter graphPresenter;

    public DashboardView(GraphType... graphTypes) throws HeadlessException {
        super(Localization.getInstance().getLanguage().pzks);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(Views.DEFAULT_WINDOW_WIDTH, Views.DEFAULT_WINDOW_HEIGHT);
        setLocation(0, Views.WINDOW_MARGIN_TOP);

        graphPresenter = UnitedGraphPresenter.createDefault(graphTypes);
        setContentPane(graphPresenter.getGraphsContainer());

        JMenuBar menuBar = new JMenuBar();
        initHeaderMenu(menuBar);
        setJMenuBar(menuBar);

        setVisible(true);
    }

    private void initHeaderMenu(JMenuBar menuBar) {
        Localization.Language lang = Localization.getInstance().getLanguage();
        {
            JMenu taskGraph = new JMenu(lang.taskGraph);
            menuBar.add(taskGraph);
            {
                JMenu newGraph = new JMenu(lang.aNew);
                taskGraph.add(newGraph);
                {
                    JMenuItem graphEditor = new JMenuItem(lang.graphEditor);
                    graphEditor.addActionListener(graphPresenter::onNewGraphEditor);
                    newGraph.add(graphEditor);

                    JMenuItem graphGenerator = new JMenuItem(lang.graphGenerator);
                    graphGenerator.addActionListener(graphPresenter::onNewGraphGenerator);
                    newGraph.add(graphGenerator);
                }

                JMenuItem openGraph = new JMenuItem(lang.open);
                openGraph.addActionListener(graphPresenter::onOpenGraph);
                taskGraph.add(openGraph);

                JMenuItem saveGraph = new JMenuItem(lang.save);
                saveGraph.addActionListener(graphPresenter::onSaveGraph);
                taskGraph.add(saveGraph);
            }
        }

        {
            JMenu systemGraph = new JMenu(lang.systemGraph);
            menuBar.add(systemGraph);
            {
                JMenuItem newSystem = new JMenuItem(lang.newSystem);
                newSystem.addActionListener(graphPresenter::onNewSystem);
                systemGraph.add(newSystem);

                JMenuItem openSystem = new JMenuItem(lang.open);
                openSystem.addActionListener(graphPresenter::onOpenSystem);
                systemGraph.add(openSystem);

                JMenuItem saveSystem = new JMenuItem(lang.save);
                saveSystem.addActionListener(graphPresenter::onSaveSystem);
                systemGraph.add(saveSystem);
            }
        }

        {
            JMenu modeling = new JMenu(lang.modeling);
            menuBar.add(modeling);
            {
                JMenuItem processorsParams = new JMenuItem(lang.processorsParams);
                processorsParams.addActionListener(graphPresenter::onProcessorsParams);
                modeling.add(processorsParams);

                JMenuItem gantDiagram = new JMenuItem(lang.gantDiagram);
                gantDiagram.addActionListener(graphPresenter::onGantDiagram);
                modeling.add(gantDiagram);

                JMenuItem validate = new JMenuItem(lang.validate);
                validate.addActionListener(graphPresenter::onValidate);
                modeling.add(validate);
            }
        }

        {
            JMenu statistics = new JMenu(lang.statistics);
            statistics.addActionListener(graphPresenter::onStatistics);
            menuBar.add(statistics);
        }

        {
            JMenu help = new JMenu(lang.help);
            menuBar.add(help);
            {
                JMenuItem about = new JMenuItem(lang.about);
                about.addActionListener(graphPresenter::onAbout);
                help.add(about);
            }
        }

        /*
        {
            JMenuItem validate = new JMenuItem(lang.validate);
            validate.addActionListener(graphPresenter::onValidate);
            menuBar.add(validate);
        }
        */
        /*
        {
            JMenuItem exit = new JMenuItem(lang.exit);
            exit.addActionListener(graphPresenter::onExit);
            menuBar.add(exit);
        }
        */
    }
}
