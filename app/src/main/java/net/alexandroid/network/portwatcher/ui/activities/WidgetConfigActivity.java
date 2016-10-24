package net.alexandroid.network.portwatcher.ui.activities;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import net.alexandroid.network.portwatcher.R;
import net.alexandroid.network.portwatcher.helpers.MyLog;
import net.alexandroid.network.portwatcher.helpers.ShPref;
import net.alexandroid.network.portwatcher.helpers.Utils;

import java.util.ArrayList;

public class WidgetConfigActivity extends AppCompatActivity implements View.OnClickListener {

    private int mAppWidgetId;
    private TextView tvHost, tvPorts, tvTitle;

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
        tvHost = (TextView) findViewById(R.id.input_host);
        tvPorts = (TextView) findViewById(R.id.input_port);
        tvTitle = (TextView) findViewById(R.id.input_title);
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

        String title = tvTitle.getText().toString().trim();
        String host = tvHost.getText().toString().trim();
        String ports = tvPorts.getText().toString().trim();
        ArrayList<Integer> list = Utils.convertStringToIntegerList(ports);
        String checkedPorts = Utils.convertIntegerListToString(list);
        tvPorts.setText(checkedPorts);

        MyLog.d("title: " + host);
        MyLog.d("checkedPorts: " + checkedPorts);

        if (title.length() == 0 || host.length() < 6 || checkedPorts.length() == 0) {
            MyLog.d("Wrong parameters");
            Snackbar.make(tvHost, R.string.wrong_params, Snackbar.LENGTH_SHORT).show();
            return;
        }
        RemoteViews remoteViews = Utils.createRemoteViewsForWidget(this, title, host, checkedPorts, mAppWidgetId);
        appWidgetManager.updateAppWidget(mAppWidgetId, remoteViews);
        saveNewWidget(title, host, checkedPorts);
        finishActivity();
    }

    private void saveNewWidget(String pTitle, String pHost, String ports) {
        try {
            ShPref.put(R.string.key_widget_title + mAppWidgetId, pTitle);
            ShPref.put(R.string.key_widget_host + mAppWidgetId, pHost);
            ShPref.put(R.string.key_widget_ports + mAppWidgetId, ports);
        } catch (Resources.NotFoundException e) {
            Crashlytics.logException(e);
        }
    }


    private void finishActivity() {
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
}
