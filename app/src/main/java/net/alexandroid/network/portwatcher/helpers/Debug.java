package net.alexandroid.network.portwatcher.helpers;

import android.app.Application;
import android.os.StrictMode;

import net.alexandroid.network.portwatcher.BuildConfig;
import net.alexandroid.network.portwatcher.MyApplication;

public class Debug {
    public static void enable(Application application) {
        if (BuildConfig.DEBUG) {
            MyLog.d("DEBUG");
            if (MyApplication.SHOW_LOGS) MyLog.d("SHOW LOGS TRUE");
            enableStrictMode();
            //LeakCanary.install(application); // For more information: https://github.com/square/leakcanary
        }
    }
    /**
     * StrictMode is a developer tool which detects things you might be doing by accident and brings them to your attention so you can fix them.
     * Used to make sure that we not doing things on the main thread that should not be.
     *
     * @see <a href="http://developer.android.com/reference/android/os/StrictMode.html">StrictMode</a>
     * Log tag: StrictMode
     */
    private static void enableStrictMode() {
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDeathOnNetwork()
                .build());
    }


}