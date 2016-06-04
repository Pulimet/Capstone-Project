package net.alexandroid.network.portwatcher.ui.activities;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import net.alexandroid.network.portwatcher.R;
import net.alexandroid.network.portwatcher.helpers.Utils;
import net.alexandroid.network.portwatcher.objects.ScanItem;
import net.alexandroid.network.portwatcher.services.ScanService;

import java.util.ArrayList;

public class WidgetConfigActivity extends AppCompatActivity implements View.OnClickListener {

    private int mAppWidgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_config);

        getAppWidgetId();


        setViews();
        setClickListener();
    }

    private void getAppWidgetId() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void setViews() {

    }


    @SuppressWarnings("ConstantConditions")
    private void setClickListener() {
        findViewById(R.id.btnOk).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOk:
                onFinishWidgetConfig();
                break;
        }
    }

    private void onFinishWidgetConfig() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.widget_layout);

        remoteViews.setTextViewText(R.id.text, "Blabla");

        Intent intent = new Intent(this, ScanService.class);
        intent.putExtra(ScanService.EXTRA_HOST, "test.com");
        intent.putExtra(ScanService.EXTRA_SCAN_ID, -1);
        intent.putIntegerArrayListExtra(ScanService.EXTRA_PORTS, Utils.convertStringToIntegerList("80"));
        startService(intent);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.text, pendingIntent);

        appWidgetManager.updateAppWidget(mAppWidgetId, remoteViews);
        finishActivity();
    }

    private void finishActivity() {
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
}
