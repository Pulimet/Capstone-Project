package net.alexandroid.network.portwatcher;

import android.app.Application;

import net.alexandroid.network.portwatcher.helpers.Contextor;
import net.alexandroid.network.portwatcher.helpers.Counter;
import net.alexandroid.network.portwatcher.helpers.Debug;

public class MyApplication extends Application {

    // ----- LOGS ------------------------------------ 
    public static final boolean SHOW_LOGS = true;
    public static final boolean SHOW_COUNTER_LOGS = true;


    @Override
    public void onCreate() {
        super.onCreate();
        Contextor.getInstance().init(getApplicationContext());
        Counter.setInitTime();
        Debug.enable(this);
    }

    public static String getMyPackageName() {
        return Contextor.getInstance().getContext().getPackageName();
    }

} 