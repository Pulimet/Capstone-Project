package net.alexandroid.network.portwatcher.helpers;

import net.alexandroid.network.portwatcher.MyApplication;

public class Counter {
    private static long initTime, lastTime;

    public static void setInitTime() {
        lastTime = 0;
        initTime = System.currentTimeMillis();
        logEventTime("Init time");
    }

    public static void logEventTime(String message) {
        StringBuilder msg = new StringBuilder(message);
        while (msg.length() < 35) msg.append(" ");

        long currTime = System.currentTimeMillis() - initTime;

        long ms = currTime % 1000;
        long s = currTime / 1000 % 60;
        long m = currTime / 1000 / 60;
        String currentTime = ": " + m + ":" + s + ":" + ms;

        long diffTime = currTime - lastTime;
        ms = diffTime % 1000;
        s = diffTime / 1000 % 60;
        m = diffTime / 1000 / 60;
        String differenceTime = ": " + m + ":" + s + ":" + ms;

        lastTime = currTime;

        if (MyApplication.SHOW_COUNTER_LOGS) {
            MyLog.iShort(msg + currentTime + "  Diff time: " + differenceTime);
        }
    }
}