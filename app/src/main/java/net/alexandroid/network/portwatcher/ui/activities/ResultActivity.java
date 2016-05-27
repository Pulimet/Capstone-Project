package net.alexandroid.network.portwatcher.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import net.alexandroid.network.portwatcher.R;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        setClickListener();
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
                // TODO: Rescan
                break;
        }
    }
}
