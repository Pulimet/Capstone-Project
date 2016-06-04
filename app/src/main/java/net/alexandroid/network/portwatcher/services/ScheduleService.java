package net.alexandroid.network.portwatcher.services;


import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.TaskParams;

import net.alexandroid.network.portwatcher.data.DbContract;
import net.alexandroid.network.portwatcher.helpers.MyLog;
import net.alexandroid.network.portwatcher.helpers.Utils;
import net.alexandroid.network.portwatcher.ui.fragments.ScheduleFragment;

public class ScheduleService extends GcmTaskService {

    public static final String EXTRA_HOST = "host";
    public static final String EXTRA_PORT = "port";

    @Override
    public void onInitializeTasks() {
        // When your package is removed or updated, all of its network tasks are cleared by
        // the GcmNetworkManager. You can override this method to reschedule them in the case of
        // an updated package. This is not called when your application is first installed.
        //
        // This is called on your application's main thread.
        // TODO(developer): In a real app, this should be implemented to re-schedule important tasks.
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentResolver cr = getContentResolver();
                Cursor c = cr.query(DbContract.ScheduleEntry.CONTENT_URI, null, null, null, null);
                if (c == null) {
                    return;
                }
                while (c.moveToNext()) {
                    String host = c.getString(ScheduleFragment.COL_HOST);
                    String ports = c.getString(ScheduleFragment.COL_PORTS);
                    String interval = c.getString(ScheduleFragment.COL_INTERVAL);

                    MyLog.d("addSchedule, host: " + host + "  ports: " + ports + "   Interval: " + interval);

                    Bundle bundle = new Bundle();
                    bundle.putString(ScheduleService.EXTRA_HOST, host);
                    bundle.putString(ScheduleService.EXTRA_PORT, ports);

                    PeriodicTask task = new PeriodicTask.Builder()
                            .setService(ScheduleService.class)
                            .setExtras(bundle)
                            .setTag(Utils.createAlarmTag(host, ports, interval))
                            .setPeriod(Long.valueOf(interval) / 1000)
                            .build();

                    GcmNetworkManager.getInstance(getApplicationContext()).schedule(task);
                }
                c.close();
            }
        }).start();

    }


    @Override
    public int onRunTask(TaskParams pTaskParams) {
        String tag = pTaskParams.getTag();


        Bundle bundle = pTaskParams.getExtras();

        String host = bundle.getString(EXTRA_HOST);
        String ports = bundle.getString(EXTRA_PORT);

        MyLog.d("host: " + host + "   ports: " + ports + "   onRunTask: " + tag);
        startScanService(host, ports);
        return 0;
    }

    private void startScanService(String pHost, String pPorts) {
        Intent intent = new Intent(ScheduleService.this, ScanService.class);
        intent.putExtra(ScanService.EXTRA_HOST, pHost);
        intent.putExtra(ScanService.EXTRA_SCAN_ID, -1);
        intent.putIntegerArrayListExtra(ScanService.EXTRA_PORTS, Utils.convertStringToIntegerList(pPorts));
        startService(intent);
    }
}
