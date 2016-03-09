package edu.kpi.nesteruk.pzcs.view.dialog;

import edu.kpi.nesteruk.util.StringUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Yurii on 12.12.2014.
 */
public abstract class Dialog {
    private static Component parentComponent;

    protected final String title;
    protected final String text;

    protected Dialog(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public abstract Object show();

    public static class InputWeightDialog extends Dialog {
        public InputWeightDialog(String title, String text) {
            super(title, text);
        }

        @Override
        public Integer show() {
            String rawInput = (String) JOptionPane.showInputDialog(
                    parentComponent,
                    text,
                    title,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    ""
            );
            return StringUtils.isEmpty(rawInput) ? null : Integer.valueOf(rawInput);
        }
    }
}
