package net.alexandroid.network.portwatcher.services;


import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

import net.alexandroid.network.portwatcher.helpers.MyLog;

public class ScheduleService extends GcmTaskService {

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
        MyLog.d("onRunTask: " + tag);

        return 0;
    }
}
