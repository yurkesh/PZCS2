package edu.kpi.nesteruk.pzcs.view.tasks;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.graph.generation.Params;
import edu.kpi.nesteruk.util.CollectionUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Created by Anatolii Bed on 2016-04-13.
 */
public class GraphGeneratorParamsInputDialog {

    public static Optional<Params> showDialog(Params defaultParams) {
        Map<ParamsInput, Pair<String, JTextField>> inputs = Arrays.stream(ParamsInput.values()).collect(CollectionUtils.CustomCollectors.toMap(
                Function.identity(),
                paramsInput -> Pair.create(paramsInput.getCaption(), field(paramsInput.getValue(defaultParams))),
                LinkedHashMap::new
        ));
        JPanel panel = new JPanel();
        GridLayout gridLayout = new GridLayout(inputs.size(), 2);
        panel.setLayout(gridLayout);
        inputs.forEach((input, captionWithField) -> {
            panel.add(new JLabel(captionWithField.first));
            panel.add(captionWithField.second);
            /*
            panel.add(Box.createHorizontalStrut(15));
            */
        });

        int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Введіть параметри",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (result == JOptionPane.OK_OPTION) {
            Params.Builder paramsBuilder = new Params.Builder();
            try {
                inputs.forEach((input, captionWithField) -> input.setValue(captionWithField.second.getText(), paramsBuilder));
            } catch (Exception e) {
                System.err.println("Помилка " + e);
            }
            return Optional.of(paramsBuilder.build());
        } else {
            return Optional.empty();
        }
    }

    private static JTextField field(Number value) {
        String strValue = String.valueOf(value);
        return field(strValue.length() + 1, strValue);
    }

    private static JTextField field(int length, String value) {
        return set(new JTextField(length), value);
    }

    private static JTextField set(JTextField field, String value) {
        field.setText(value);
        return field;
    }

}
