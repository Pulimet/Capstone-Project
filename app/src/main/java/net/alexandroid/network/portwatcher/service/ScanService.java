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
import net.alexandroid.network.portwatcher.objects.HostAndPorts;
import net.alexandroid.network.portwatcher.task.PortScanManager;
import net.alexandroid.network.portwatcher.task.ScanResult;

import java.util.ArrayList;


public class ScanService extends Service {

    public static final String EXTRA_HOST = "host";
    public static final String EXTRA_PORTS = "ports";

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    @Override
    public void onCreate() {
        super.onCreate();
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

        String host = intent.getStringExtra(EXTRA_HOST);
        ArrayList<Integer> portsList = intent.getIntegerArrayListExtra(EXTRA_PORTS);
        HostAndPorts hostAndPorts = new HostAndPorts(host, portsList);

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = hostAndPorts;
        mServiceHandler.sendMessage(msg);

        return START_REDELIVER_INTENT;
    }


    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler implements ScanResult {
        private int serviceId;
        private int scanCount;
        private int scanTotal;

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            serviceId = msg.arg1;

            HostAndPorts hostAndPorts = (HostAndPorts) msg.obj;
            MyLog.d("TEST host: " + hostAndPorts.getHost());
            scanTotal = hostAndPorts.getPortsList().size();
            for (Integer port : hostAndPorts.getPortsList()) {
                PortScanManager.startScanTask(hostAndPorts.getHost(), port, this);
            }



        }

        @Override
        public void onResult(String host, int port, int state) {
            scanCount++;

            // TODO Event bus on sing port result

            if (scanCount == scanTotal) {
                // TODO Event bus on scan of list finished
                // TODO Show notification if UI unavailable
                stopService();
            }
        }

        private void stopService() {
            // Stop the service using the startId, so that we don't stop the service in the middle of handling another job
            stopSelf(serviceId);
            MyLog.d("stopSelf, startId:" + serviceId);
        }
    }


}
