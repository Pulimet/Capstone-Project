package net.alexandroid.network.portwatcher.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;

import net.alexandroid.network.portwatcher.helpers.MyLog;
import net.alexandroid.network.portwatcher.task.PortScanManager;


public class ScanService extends Service {

    public static final String EXTRA_HOST = "host";
    public static final String EXTRA_PORTS = "ports";

    private PortScanManager mPortScanManager;
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mPortScanManager = PortScanManager.getInstance();

        HandlerThread thread = new HandlerThread("ServiceStartArguments", HandlerThread.NORM_PRIORITY);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyLog.d("onStartCommand, startId:" + startId);

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        return START_REDELIVER_INTENT;
    }


    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {


            // Stop the service using the startId, so that we don't stop the service in the middle of handling another job
            stopSelf(msg.arg1);
            MyLog.d("stopSelf, startId:" + msg.arg1);
        }
    }



}
