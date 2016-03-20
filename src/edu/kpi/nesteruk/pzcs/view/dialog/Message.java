package edu.kpi.nesteruk.pzcs.view.dialog;

import javax.swing.*;

/**
 * Created by Yurii on 2016-03-20.
 */
public class Message {

    public static void showMessage(boolean error, String title, String text) {
        JOptionPane.showMessageDialog(
                null,
                text,
                title,
                error ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE
        );
    }

}
