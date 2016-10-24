package net.alexandroid.network.portwatcher.widget;


import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.res.Resources;
import android.widget.RemoteViews;

import com.crashlytics.android.Crashlytics;

import net.alexandroid.network.portwatcher.R;
import net.alexandroid.network.portwatcher.helpers.ShPref;
import net.alexandroid.network.portwatcher.helpers.Utils;

public class WidgetProvider extends AppWidgetProvider {


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            String title = null;
            String host = null;
            String ports = null;
            try {
                title = ShPref.getString(R.string.key_widget_title + appWidgetId, "");
                host = ShPref.getString(R.string.key_widget_host + appWidgetId, "");
                ports = ShPref.getString(R.string.key_widget_ports + appWidgetId, "");
            } catch (Resources.NotFoundException e) {
                Crashlytics.logException(e);
            }
            RemoteViews remoteViews = Utils.createRemoteViewsForWidget(context, title, host, ports, appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    //Widget instance is removed from the home screen.
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            ShPref.remove(R.string.key_widget_title + appWidgetId);
            ShPref.remove(R.string.key_widget_host + appWidgetId);
            ShPref.remove(R.string.key_widget_ports + appWidgetId);
        }
        super.onDeleted(context, appWidgetIds);
    }


    //Called the first time an instance of your widget is added to the home screen.
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    //Called once the last instance of your widget is removed from the home screen.
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }
}
