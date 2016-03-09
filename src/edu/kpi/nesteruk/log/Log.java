package edu.kpi.nesteruk.log;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Yurii on 2016-02-17.
 */
public class Log {

    static final String INFO = "INFO";
    static final String DEBUG = "DEBUG";
    static final String ERROR = "ERROR";

    public static void i(String tag, Object message) {
        print(false, DEBUG, tag, nullabe(message));
    }

    public static void d(String tag, Object message) {
        print(false, DEBUG, tag, nullabe(message));
    }

    public static void e(String tag, Object message) {
        print(true, ERROR, tag, nullabe(message));
    }

    public static void e(String tag, Object message, Throwable t) {
        e(tag, nullabe(message) + "\n" + getStackTraceString(t));
    }

    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }

        // This is to reduce the amount of log spew that apps do in the non-error
        // condition of the network being unavailable.
        Throwable t = tr;
        while (t != null) {
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    public static void print(boolean error, String mask, String tag, String what) {
        PrintStream ps = error ? System.err : System.out;
        ps.println(mask + ":\t[" + tag + "]\t" + what);
    }

    public static String nullabe(Object message) {
        return message == null ? "null" : message.toString();
    }
}
