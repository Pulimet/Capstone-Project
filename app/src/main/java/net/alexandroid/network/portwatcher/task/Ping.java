package net.alexandroid.network.portwatcher.task;


import net.alexandroid.network.portwatcher.helpers.MyLog;

import java.io.IOException;

public class Ping implements Runnable {

    public static final int TIMEOUT = 5000;

    private String strHost;
    private CallBack mCallBack;

    public Ping(String pStrHost, CallBack pCallBack) {
        strHost = pStrHost;
        mCallBack = pCallBack;
    }

    @Override
    public void run() {
        new Thread(timeOut).start();

        boolean result = pingHost(strHost);
        if (mCallBack != null) {
            mCallBack.onResult(strHost, result);
            mCallBack = null;
        }
    }

    private Runnable timeOut = new Runnable() {
        @Override
        public void run() {
            MyLog.d("Start timer");
            try {
                Thread.sleep(TIMEOUT);
            } catch (InterruptedException pE) {
                pE.printStackTrace();
            }
            MyLog.d("End timer");
            onTimeOut();
        }
    };

    private void onTimeOut() {
        if (mCallBack != null) {
            mCallBack.onResult(strHost, false);
            mCallBack = null;
        }
    }


    private boolean pingHost(String host) {
        MyLog.d("pingHost");
        Runtime runtime = Runtime.getRuntime();
        try {
            Process mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 " + host);
            int mExitValue = mIpAddrProcess.waitFor();
            MyLog.d(" mExitValue " + mExitValue);
            return mExitValue == 0;
        } catch (InterruptedException | IOException ex) {
            ex.printStackTrace();
            MyLog.d("Exception:" + ex);
        }
        return false;
    }

    public interface CallBack {
        void onResult(String strHost, boolean pingResult);
    }
}
