package net.alexandroid.network.portwatcher.services;


import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

import net.alexandroid.network.portwatcher.helpers.MyLog;
import net.alexandroid.network.portwatcher.helpers.Utils;

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
    }


    @Override
    public int onRunTask(TaskParams pTaskParams) {
        String tag = pTaskParams.getTag();


        Bundle bundle = pTaskParams.getExtras();

        String host = bundle.getString(EXTRA_HOST);
        String ports = bundle.getString(EXTRA_PORT);

        MyLog.d("host: " + host + "   ports: " + ports + "   onRunTask: " + tag);

        Intent intent = new Intent(ScheduleService.this, ScanService.class);
        intent.putExtra(ScanService.EXTRA_HOST, host);
        intent.putExtra(ScanService.EXTRA_SCAN_ID, -1);
        intent.putIntegerArrayListExtra(ScanService.EXTRA_PORTS, Utils.convertStringToIntegerList(ports));
        startService(intent);

        return 0;
    }
}
