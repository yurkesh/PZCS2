package edu.kpi.nesteruk.pzcs.view.processors;

import edu.kpi.nesteruk.pzcs.planning.PlanningParams;
import edu.kpi.nesteruk.pzcs.view.tasks.ParamsInput;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Created by Yurii on 2016-06-02.
 */
public class PlanningParamsFrame extends JFrame {

    public PlanningParamsFrame(PlanningParams params, Consumer<PlanningParams> planningListener) {
        super("Планування");

        JPanel mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);

        JPanel valuesPanel = new JPanel(new GridLayout(ParamsInput.values().length, 2));
        addOntoPanel(valuesPanel, params);
        mainPanel.add(valuesPanel, BorderLayout.NORTH);

        JButton generateBtn = new JButton("Виконати планування");
        generateBtn.addActionListener(action -> planningListener.accept(params));
        mainPanel.add(generateBtn, BorderLayout.CENTER);
        pack();
        setVisible(true);
    }

    private void addOntoPanel(JPanel mainPanel, PlanningParams params) {
        Arrays.stream(PlanningParamInput.values()).forEach(paramsInput -> {
            mainPanel.add(new JLabel(paramsInput.getCaption()));
            JTextField valueField = new JTextField(String.valueOf(paramsInput.getValue(params)));
            valueField.setEditable(false);
            mainPanel.add(valueField);
        });
    }
}
