package edu.kpi.nesteruk.pzcs.view.common;

import edu.kpi.nesteruk.misc.GenericBuilder;
import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.view.common.input.ParamsInput;
import edu.kpi.nesteruk.util.CollectionUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Yurii on 2016-06-09.
 */
public class GenericInputDialog {
    
    public static <Params, Builder extends GenericBuilder<Params>, Input extends ParamsInput<Params, Builder>> void showDialog(
            String title,
            Params defaultParams,
            Collection<Input> inputs,
            Supplier<Builder> builderFactory,
            Consumer<Params> paramsConsumer) {
        Map<Input, Pair<String, JTextField>> inputsMap = inputs.stream().collect(CollectionUtils.CustomCollectors.toMap(
                Function.identity(),
                paramsInput -> Pair.create(paramsInput.getCaption(), field(paramsInput.getValue(defaultParams))),
                LinkedHashMap::new
        ));
        JPanel panel = new JPanel(new GridBagLayout());
        inputsMap.forEach((input, captionWithField) -> {
            panel.add(new JLabel(captionWithField.first));
            panel.add(captionWithField.second);
            panel.add(Box.createHorizontalStrut(15));
        });

        int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                title,
                JOptionPane.OK_CANCEL_OPTION
        );
        if (result == JOptionPane.OK_OPTION) {
            Builder paramsBuilder = builderFactory.get();
            try {
                inputsMap.forEach((input, captionWithField) ->
                        input.setValue(captionWithField.second.getText(), paramsBuilder)
                );
            } catch (Exception e) {
                System.err.println("Cannot interpret parameter value. Exception = " + e);
                e.printStackTrace();
            }
            try {
                Params params = paramsBuilder.build();
                paramsConsumer.accept(params);
            } catch (Exception e) {
                System.err.println("Cannot use specified params. Exception = " + e);
                e.printStackTrace();
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

}
