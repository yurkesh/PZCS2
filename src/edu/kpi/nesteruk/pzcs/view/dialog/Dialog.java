package edu.kpi.nesteruk.pzcs.view.dialog;

import edu.kpi.nesteruk.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

/**
 * Created by Anatolii on 12.12.2014.
 */
public abstract class Dialog<Result> {

    protected final String title;
    protected final String text;
    protected final String initialValue;

    public Dialog(String title, String text, String initialValue) {
        this.title = title;
        this.text = text;
        this.initialValue = initialValue;
    }

    protected Dialog(String title, String text) {
        this(title, text, "");
    }

    public abstract Optional<Result> show();

    public static class InputTextDialog extends Dialog<String> {

        public InputTextDialog(String title, String text) {
            super(title, text);
        }

        public InputTextDialog(String title, String text, String initialValue) {
            super(title, text, initialValue);
        }

        @Override
        public Optional<String> show() {
            return Optional.ofNullable((String) JOptionPane.showInputDialog(
                    null,
                    text,
                    title,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    initialValue
            ));
        }
    }

    public static class InputIntegerDialog extends InputTextDialog {

        public InputIntegerDialog(String title, String text) {
            super(title, text);
        }

        public InputIntegerDialog(String title, String text, String initialValue) {
            super(title, text, initialValue);
        }

        public Optional<Integer> showFetchInt() {
            Optional<String> result = super.show();
            return result.map(rawInput -> {
                if(StringUtils.isEmpty(rawInput)) {
                   return null;
                } else {
                    try {
                        return Integer.valueOf(rawInput);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
            });
        }
    }


}
