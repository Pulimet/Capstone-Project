package net.alexandroid.network.portwatcher.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.SparseIntArray;

import net.alexandroid.network.portwatcher.MyApplication;
import net.alexandroid.network.portwatcher.R;
import net.alexandroid.network.portwatcher.data.DbContract;
import net.alexandroid.network.portwatcher.data.DbHelper;
import net.alexandroid.network.portwatcher.events.PortScanFinishEvent;
import net.alexandroid.network.portwatcher.helpers.MyLog;
import net.alexandroid.network.portwatcher.helpers.Utils;
import net.alexandroid.network.portwatcher.objects.HostAndPorts;
import net.alexandroid.network.portwatcher.objects.ScanItem;
import net.alexandroid.network.portwatcher.task.PortScanManager;
import net.alexandroid.network.portwatcher.task.ScanResult;
import net.alexandroid.network.portwatcher.ui.activities.ResultActivity;
import net.alexandroid.network.portwatcher.ui.fragments.ScanFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;


public class ScanService extends Service {

    public static final String EXTRA_SCAN_ID = "scan_id";
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
        int scanId = intent.getIntExtra(EXTRA_SCAN_ID, 0);
        MyLog.d("onStartCommand, startId:" + startId + "   scanId: " + scanId);

        String host = intent.getStringExtra(EXTRA_HOST);
        ArrayList<Integer> portsList = intent.getIntegerArrayListExtra(EXTRA_PORTS);
        HostAndPorts hostAndPorts = new HostAndPorts(host, portsList);

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.arg2 = scanId;
        msg.obj = hostAndPorts;
        mServiceHandler.sendMessage(msg);

        return START_REDELIVER_INTENT;
    }


    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler implements ScanResult {
        private int scanId;
        private int serviceId;
        private int scanTotal;
        private SparseIntArray scanResults = new SparseIntArray();

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            serviceId = msg.arg1;
            scanId = msg.arg2;
            HostAndPorts hostAndPorts = (HostAndPorts) msg.obj;
            MyLog.d("TEST host: " + hostAndPorts.getHost());
            scanTotal = hostAndPorts.getPortsList().size();
            for (Integer port : hostAndPorts.getPortsList()) {
                PortScanManager.startScanTask(hostAndPorts.getHost(), port, this);
            }
        }

        @Override
        public void onResult(String host, int port, int state) {
            scanResults.put(port, state);
            if (scanId == ScanFragment.sScanId) {
                EventBus.getDefault().post(new PortScanFinishEvent(host, scanResults, scanResults.size() == scanTotal));
            }
            if (scanResults.size() == scanTotal) {

                if (!MyApplication.isScanFragmentVisible() || scanId != ScanFragment.sScanId) {
                    showNotification(host);
                }

                addResultToHistory(host);
                stopService();
            }
        }

        private void showNotification(String host) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.scan_results) + " " + host);

            StringBuilder strText = new StringBuilder();
            String openPorts = Utils.convertSpareIntArrToOpenPortsString(scanResults);
            if (openPorts.length() > 0) {
                strText.append(getString(R.string.open));
                strText.append(openPorts);
            }
            String closedPorts = Utils.convertSpareIntArrToClosePortsString(scanResults);
            if (closedPorts.length() > 0) {
                strText.append(getString(R.string.closed));
                strText.append(closedPorts);
            }
            builder.setContentText(strText.toString());

            builder.setAutoCancel(true);

            builder.setStyle(new NotificationCompat.BigTextStyle());

            //builder.setLargeIcon(bitmap);

            // Android 5+
            //builder.setColor(context.getResources().getColor(R.color.colorPrimaryDark));


            // MAIN INTENT
            builder.setContentIntent(getShowResultsPendingIntent(host));

            //builder.addAction(new NotificationCompat.Action(R.drawable.ic_menu_edit, "TEST", pendingIntentForAction));


            Notification notification = builder.build();
            NotificationManagerCompat.from(getApplicationContext()).notify(scanId, notification);
        }

        private PendingIntent getShowResultsPendingIntent(String host) {
            ScanItem scanItem = new ScanItem(
                    host,
                    Utils.convertSpareIntArrToPortsString(scanResults),
                    String.valueOf(System.currentTimeMillis()),
                    Utils.convertSpareIntArrToOpenPortsString(scanResults)
            );

            Intent intentShowResults = new Intent(ScanService.this, ResultActivity.class);
            intentShowResults.putExtra(ResultActivity.EXTRA_SCAN_ITEM, scanItem);
            return PendingIntent.getActivity(
                    ScanService.this,
                    0,
                    intentShowResults,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
        }

        private void addResultToHistory(String host) {
            MyLog.d("addResultToHistory, scanId: " + scanId);
            ContentResolver contentResolver = getApplicationContext().getContentResolver();
            ContentValues contentValues =
                    DbHelper.getHistoryContentValues(
                            host,
                            Utils.convertSpareIntArrToPortsString(scanResults),
                            Utils.convertSpareIntArrToOpenPortsString(scanResults),
                            System.currentTimeMillis());
            contentResolver.insert(DbContract.HistoryEntry.CONTENT_URI, contentValues);
        }


        private void stopService() {
            // Stop the service using the startId, so that we don't stop the service in the middle of handling another job
            stopSelf(serviceId);
            MyLog.d("stopSelf, startId:" + serviceId);
        }
    }


}
