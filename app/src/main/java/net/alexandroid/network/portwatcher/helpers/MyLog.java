package net.alexandroid.network.portwatcher.helpers;

import android.util.Log;

import net.alexandroid.network.portwatcher.MyApplication;

import java.text.MessageFormat;

public class MyLog {
    public static final String TAG = "XSW - ";

    public static void d(String msg) {
        if (MyApplication.SHOW_LOGS) {
            logIt(Log.DEBUG, msg);
        }
    }

    public static void e(String msg) {
        if (MyApplication.SHOW_LOGS) {
            logIt(Log.ERROR, msg);
        }
    }

    public static void d(String... args) {
        if (MyApplication.SHOW_LOGS) {
            StringBuilder str = new StringBuilder();
            for (String s : args) {
                str.append(s).append("\t| ");
            }
            logIt(Log.DEBUG, str.toString());
        }
    }

    public static void iShort(String msg) {
        if (MyApplication.SHOW_LOGS) {
            logItShort(Log.INFO, msg);
        }
    }

    private static void logIt(int level, String msg) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace != null && stackTrace.length > 4) {
            StackTraceElement element = stackTrace[4];
            String fullClassName = element.getClassName();

            StringBuilder simpleClassName = new StringBuilder(fullClassName.replace(MyApplication.getMyPackageName(), ""));
            while (simpleClassName.length() < 35) simpleClassName.append(" ");

            StringBuilder methodName = new StringBuilder(element.getMethodName());
            methodName.append("()");
            while (methodName.length() < 25) methodName.append(" ");

            StringBuilder threadId = new StringBuilder(String.valueOf(Thread.currentThread().getId()));
            while (threadId.length() < 6) threadId.append(" ");

            msg = MessageFormat.format("T:{0} | {1} # {2} => {3}", threadId, simpleClassName, methodName, msg);
            Log.println(level, TAG, msg);
        }
    }

    private static void logItShort(int level, String msg) {
        StringBuilder threadId = new StringBuilder(String.valueOf(Thread.currentThread().getId()));
        while (threadId.length() < 6) threadId.append(" ");

        msg = MessageFormat.format("T:{0} | => {1}", threadId, msg);
        Log.println(level, TAG, msg);
    }
}
