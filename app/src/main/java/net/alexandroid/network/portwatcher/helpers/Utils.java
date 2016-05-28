package net.alexandroid.network.portwatcher.helpers;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;

public class Utils {

    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        } catch (Exception e) {
            return false;
        }
    }

    public static ArrayList<Integer> getPortListFromString(String ports) {
        ArrayList<Integer> list = new ArrayList<>();
        String regex = "[^0-9,\\-]";
        String result = ports.replaceAll(regex, "");

        String[] lines = result.split(",");
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].contains("-")) {
                String[] p = lines[i].split("\\-");
                if (p.length > 1) {
                    if (p[0].length() > 0 && p[1].length() > 0
                            && p[0].length() < 6 && p[1].length() < 6) {
                        int from = Integer.valueOf(p[0]);
                        int to = Integer.valueOf(p[1]);
                        if (from < to && to < 65535 && from > 0) {
                            for (int a = from; a <= to; a++) {
                                list.add(a);
                            }
                        }
                    }
                }
            } else {
                if (lines[i].length() > 0 && lines[i].length() < 6) {
                    int check_port = Integer.valueOf(lines[i]);
                    if (check_port > 0 & check_port < 65536)
                        list.add(check_port);
                }
            }
        }
        return list;
    }
}
