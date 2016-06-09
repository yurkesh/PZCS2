package edu.kpi.nesteruk.pzcs.view.tasks;

import edu.kpi.nesteruk.pzcs.graph.generation.GeneratorParams;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Created by Anatolii Bed on 2016-04-13.
 */
public class GraphGeneratorFrame extends JFrame {

    public GraphGeneratorFrame(GeneratorParams generatorParams, Consumer<GeneratorParams> generateListener) {
        super("Генератор графу");

        JPanel mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);

        JPanel valuesPanel = new JPanel(new GridLayout(TasksGraphGeneratorParamsInput.values().length, 2));
        addOntoPanel(valuesPanel, generatorParams);
        mainPanel.add(valuesPanel, BorderLayout.NORTH);

        JButton generateBtn = new JButton("Генерувати");
        generateBtn.addActionListener(action -> generateListener.accept(generatorParams));
        mainPanel.add(generateBtn, BorderLayout.CENTER);
        pack();
        setVisible(true);
    }

    private void addOntoPanel(JPanel mainPanel, GeneratorParams generatorParams) {
        Arrays.stream(TasksGraphGeneratorParamsInput.values()).forEach(paramsInput -> {
            mainPanel.add(new JLabel(paramsInput.getCaption()));
            JTextField valueField = new JTextField(String.valueOf(paramsInput.getValue(generatorParams)));
            valueField.setEditable(false);
            mainPanel.add(valueField);
        });
    }
}
