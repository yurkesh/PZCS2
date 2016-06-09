package edu.kpi.nesteruk.pzcs.view.processors;

import edu.kpi.nesteruk.pzcs.planning.PlanningParams;
import edu.kpi.nesteruk.pzcs.view.common.GenericInputDialog;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Created by Yurii on 2016-04-13.
 */
public class PlanningParamsInputDialog {

    public static void showDialog(PlanningParams defaultParams, Consumer<PlanningParams> paramsConsumer) {
        GenericInputDialog.showDialog(
                "Enter scheduling params",
                defaultParams,
                Arrays.asList(PlanningParamInput.values()),
                PlanningParams.Builder::new,
                paramsConsumer
        );
    }

    /*
    public static void showDialog(PlanningParams defaultParams, Consumer<PlanningParams> paramsConsumer) {
        Map<PlanningParamInput, Pair<String, JTextField>> inputs = Arrays.stream(PlanningParamInput.values()).collect(CollectionUtils.CustomCollectors.toMap(
                Function.identity(),
                paramsInput -> Pair.create(paramsInput.getCaption(), field(paramsInput.getValue(defaultParams))),
                LinkedHashMap::new
        ));
        JPanel panel = new JPanel();
        inputs.forEach((input, captionWithField) -> {
            panel.add(new JLabel(captionWithField.first));
            panel.add(captionWithField.second);
            panel.add(Box.createHorizontalStrut(15));
        });

        int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Введіть параметри планування",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (result == JOptionPane.OK_OPTION) {
            PlanningParams.Builder paramsBuilder = new PlanningParams.Builder();
            try {
                inputs.forEach((input, captionWithField) ->
                        input.setValue(captionWithField.second.getText(), paramsBuilder)
                );
            } catch (Exception e) {
                System.err.println("Неможливо інтерпритувати значення параметра. Виключення = " + e);
            }
            try {
                PlanningParams params = paramsBuilder.build();
                paramsConsumer.accept(params);
            } catch (Exception e) {
                System.err.println("Неможливо використати вказані параметри. Виключення = " + e);
            }
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
    */

}
