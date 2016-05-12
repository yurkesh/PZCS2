package edu.kpi.nesteruk.pzcs.view.tasks;

import edu.kpi.nesteruk.pzcs.graph.generation.Params;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Created by Yurii on 2016-04-13.
 */
public class GraphGeneratorFrame extends JFrame {

    public GraphGeneratorFrame(Params params, Consumer<Params> generateListener) {
        super("Генератор графу");

        JPanel mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);

        JPanel valuesPanel = new JPanel(new GridLayout(ParamsInput.values().length, 2));
        addOntoPanel(valuesPanel, params);
        mainPanel.add(valuesPanel, BorderLayout.NORTH);

        JButton generateBtn = new JButton("Генерувати");
        generateBtn.addActionListener(action -> generateListener.accept(params));
        mainPanel.add(generateBtn, BorderLayout.CENTER);
        pack();
        setVisible(true);
    }

    private void addOntoPanel(JPanel mainPanel, Params params) {
        Arrays.stream(ParamsInput.values()).forEach(paramsInput -> {
            mainPanel.add(new JLabel(paramsInput.getCaption()));
            JTextField valueField = new JTextField(String.valueOf(paramsInput.getValue(params)));
            valueField.setEditable(false);
            mainPanel.add(valueField);
        });
    }
}
