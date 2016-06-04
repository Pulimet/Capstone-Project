package net.alexandroid.network.portwatcher.helpers;


import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.SparseIntArray;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import net.alexandroid.network.portwatcher.task.PortScanRunnable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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

    public static ArrayList<Integer> convertStringToIntegerList(String ports) {
        ArrayList<Integer> list = new ArrayList<>();
        String regex = "[^0-9,\\-]";
        String result = ports.replaceAll(regex, "");

        String[] lines = result.split(",");
        for (String line : lines) {
            if (line.contains("-")) {
                String[] p = line.split("\\-");
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
                if (line.length() > 0 && line.length() < 6) {
                    int check_port = Integer.valueOf(line);
                    if (check_port > 0 & check_port < 65536)
                        list.add(check_port);
                }
            }
        }
        return list;
    }

    public static String convertIntegerListToString(ArrayList<Integer> list) {
        StringBuilder stringBuilder = new StringBuilder();
        int firstRangeNum = -1;
        for (int i = 0; i < list.size(); i++) {
            if (i + 1 < list.size() && list.get(i + 1) - 1 == list.get(i)) {
                if (firstRangeNum < 0) {
                    firstRangeNum = list.get(i);
                }
            } else {
                if (firstRangeNum > -1) {
                    stringBuilder.append(firstRangeNum);
                    stringBuilder.append("-");
                    firstRangeNum = -1;
                }
                stringBuilder.append(list.get(i));
                if (i + 1 < list.size()) {
                    stringBuilder.append(",");
                }
            }

        }

        return stringBuilder.toString();
    }

    public static String convertSpareIntArrToPortsString(SparseIntArray scanResults) {
        ArrayList<Integer> list = new ArrayList<>();

        for (int i = 0; i < scanResults.size(); i++) {
            int key = scanResults.keyAt(i); // port num
            list.add(key);
        }
        return convertIntegerListToString(list);
    }

    public static String convertSpareIntArrToOpenPortsString(SparseIntArray scanResults) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < scanResults.size(); i++) {
            int key = scanResults.keyAt(i); // port num
            // get the object by the key.
            int value = scanResults.get(key); // state of port
            if (value == PortScanRunnable.OPEN) {
                list.add(key);
            }
        }
        return convertIntegerListToString(list);
    }

    public static String convertSpareIntArrToClosePortsString(SparseIntArray scanResults) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < scanResults.size(); i++) {
            int key = scanResults.keyAt(i); // port num
            // get the object by the key.
            int value = scanResults.get(key); // state of port
            if (value != PortScanRunnable.OPEN) {
                list.add(key);
            }
        }
        return convertIntegerListToString(list);
    }

    public static int getDpInPixels(int dpValue) {
        Context context = Contextor.getInstance().getContext();
        float d = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * d);
    }

    public static void hideKeyboard(Context pContext, EditText pEditText) {
        InputMethodManager imm = (InputMethodManager) pContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(pEditText.getWindowToken(), 0);
        }
    }

    public static void appendRedText(StringBuilder pResult, Integer num) {
        pResult.append("<font color=#FF0000>");
        pResult.append(num);
        pResult.append("</font> ");

    }

    public static void appendRedText(StringBuilder pResult, String str) {
        pResult.append("<font color=#FF0000>");
        pResult.append(str);
        pResult.append("</font> ");
    }

    public static void appendGreenText(StringBuilder pResult, Integer num) {
        pResult.append("<font color=#4CAF50>");
        pResult.append(num);
        pResult.append("</font> ");


    }    public static void appendGreenText(StringBuilder pResult, String str) {
        pResult.append("<font color=#4CAF50>");
        pResult.append(str);
        pResult.append("</font> ");
    }

    public static String convertTimeFormMs(String str) {
        long dateTime = Long.valueOf(str);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy  HH:mm", Locale.getDefault());
        Date resultDate = new Date(dateTime);
        return sdf.format(resultDate);
    }

    public static Bitmap getBitmap(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getBitmap((VectorDrawable) drawable);
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    public static String formatInterval(String pInterval) {
        return "";
    }
}
