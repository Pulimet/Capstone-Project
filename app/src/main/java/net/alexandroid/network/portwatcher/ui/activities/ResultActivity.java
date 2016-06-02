package net.alexandroid.network.portwatcher.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import net.alexandroid.network.portwatcher.R;
import net.alexandroid.network.portwatcher.helpers.MyLog;
import net.alexandroid.network.portwatcher.helpers.Utils;
import net.alexandroid.network.portwatcher.objects.ScanItem;
import net.alexandroid.network.portwatcher.services.ScanService;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_SCAN_ITEM = "scan_item";
    private ScanItem mScanItem;
    private TextView tvResults;
    private ArrayList<Integer> allPorts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        mScanItem = getIntent().getParcelableExtra(EXTRA_SCAN_ITEM);

        setViews();
        setResults();
        setClickListener();
    }

    @SuppressWarnings("ConstantConditions")
    private void setViews() {
        ((TextView) findViewById(R.id.tvWhen)).setText(Utils.convertTimeFormMs(mScanItem.getStrDateTime()));
        ((TextView) findViewById(R.id.tvQuery)).setText(mScanItem.getStrHost());
        tvResults = (TextView) findViewById(R.id.tvResult);
    }

    private void setResults() {

        StringBuilder result = new StringBuilder();
        allPorts = Utils.convertStringToIntegerList(mScanItem.getStrPorts());
        ArrayList<Integer> openPorts = Utils.convertStringToIntegerList(mScanItem.getStrWereOpen());

        for (Integer port : allPorts) {
            MyLog.d("'Port: " + port);
            if (openPorts.contains(port)) {
                Utils.appendGreenText(result, port);
            } else {
                Utils.appendRedText(result, port);
            }
        }

        //tempLastScanResult = result.toString();
        tvResults.setText(Html.fromHtml(result.toString()));
    }

    @SuppressWarnings("ConstantConditions")
    private void setClickListener() {
        findViewById(R.id.btnRescan).setOnClickListener(this);
        findViewById(R.id.btnOk).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOk:
                finish();
                break;
            case R.id.btnRescan:
                startScanningService();
                Snackbar.make(v, R.string.rescan2, Snackbar.LENGTH_SHORT).show();
                //finish(); // If uncomment snack bar isn't shown
                break;
        }
    }

    private void startScanningService() {
        Intent intent = new Intent(ResultActivity.this, ScanService.class);
        intent.putExtra(ScanService.EXTRA_HOST, mScanItem.getStrHost());
        intent.putExtra(ScanService.EXTRA_SCAN_ID, -1); // ScanId is -1
        intent.putIntegerArrayListExtra(ScanService.EXTRA_PORTS, allPorts);
        startService(intent);
    }
}
