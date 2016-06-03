package net.alexandroid.network.portwatcher.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.alexandroid.network.portwatcher.MyApplication;
import net.alexandroid.network.portwatcher.R;
import net.alexandroid.network.portwatcher.data.DbContract;
import net.alexandroid.network.portwatcher.events.PortScanFinishEvent;
import net.alexandroid.network.portwatcher.helpers.MyLog;
import net.alexandroid.network.portwatcher.helpers.Utils;
import net.alexandroid.network.portwatcher.objects.ScanItem;
import net.alexandroid.network.portwatcher.services.ScanService;
import net.alexandroid.network.portwatcher.task.PingRunnable;
import net.alexandroid.network.portwatcher.task.PortScanRunnable;
import net.alexandroid.network.portwatcher.ui.activities.MainActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@lin OnListOfMainFragmentInteractionListener}
 * interface.
 */
public class ScanFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        View.OnClickListener,
        TextWatcher {

    public static int sScanId;

    public static final int COL_ID = 0;
    public static final int COL_TITLE = 1;
    public static final int COL_PORTS = 2;

    private static final int BUTTONS_LOADER = 1;
    private static boolean sPingResult;

    private int numOfButtons;

    private String tempLastScanResult;

    private TextView tvQuery, tvStatus, tvResult;
    private ProgressBar pingProgressBar, scanProgressBar;
    private TextInputLayout inputLayoutPort;
    private EditText inputPort;
    private ImageView btnRePing;
    private Button btnScan;
    private LinearLayout btnsLayout, tempLayout;

    private LinearLayout.LayoutParams btnParams, layuotParams;
    private ScanFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ScanFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        setMyParams();
        setViews(view);
        setListeners();

        if (savedInstanceState == null) {
            sScanId++;
            start();
        } else {
            tvQuery.setText(MainActivity.strLastQuery);

            pingProgressBar.setVisibility(View.GONE);
            btnRePing.setVisibility(View.VISIBLE);
            tvStatus.setText(sPingResult ? getString(R.string.success) : getString(R.string.fail));
            tvStatus.setTextColor(sPingResult ? getResources().getColor(R.color.colorAccent) : Color.RED);
            if (tempLastScanResult != null) {
                tempLastScanResult = savedInstanceState.getString("tempLastScanResult");
                tvResult.setText(Html.fromHtml(tempLastScanResult));
            }
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("tempLastScanResult", tempLastScanResult);
        super.onSaveInstanceState(outState);
    }

    private void setMyParams() {
        layuotParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layuotParams.topMargin = Utils.getDpInPixels(10) * -1;
        btnParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnParams.weight = 1;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        MyLog.d("initLoader (BUTTONS_LOADER)");
        getLoaderManager().initLoader(BUTTONS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getLoaderManager().restartLoader(BUTTONS_LOADER, null, this);
        EventBus.getDefault().register(this);

        if (context instanceof ScanFragmentInteractionListener) {
            mListener = (ScanFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        EventBus.getDefault().unregister(this);
        mListener = null;
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();
        MyApplication.setScanFragmentVisible(true);
    }


    @Override
    public void onStop() {
        super.onStop();
        MyApplication.setScanFragmentVisible(false);
    }

    private void setViews(View v) {
        tvQuery = (TextView) v.findViewById(R.id.tvQuery);
        tvStatus = (TextView) v.findViewById(R.id.tvStatus);
        tvResult = (TextView) v.findViewById(R.id.tvResult);
        pingProgressBar = (ProgressBar) v.findViewById(R.id.pingProgressBar);
        scanProgressBar = (ProgressBar) v.findViewById(R.id.scanProgressBar);
        inputLayoutPort = (TextInputLayout) v.findViewById(R.id.input_layout_port);
        inputPort = (EditText) v.findViewById(R.id.input_port);
        btnRePing = (ImageView) v.findViewById(R.id.btnRePing);
        btnScan = (Button) v.findViewById(R.id.btnScan);
        btnsLayout = (LinearLayout) v.findViewById(R.id.buttonsLayout);
    }

    private void setListeners() {
        inputPort.addTextChangedListener(this);
        btnRePing.setOnClickListener(this);
        btnScan.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (!Utils.isNetworkAvailable(getContext().getApplicationContext())) {
            MyLog.d("connection problem");
            Snackbar.make(v, R.string.network_not_available, Snackbar.LENGTH_SHORT).show();
            return;
        }
        switch (v.getId()) {
            case R.id.btnRePing: {
                tvStatus.setText("");
                pingProgressBar.setVisibility(View.VISIBLE);
                btnRePing.setVisibility(View.INVISIBLE);
                checkAndPing();
                break;
            }
            case R.id.btnScan: {
                Utils.hideKeyboard(getContext().getApplicationContext(), inputPort);
                validatePort();
                String ports = inputPort.getText().toString().trim();
                ArrayList<Integer> list = Utils.convertStringToIntegerList(ports);
                String checkedPorts = Utils.convertIntegerListToString(list);
                inputPort.setText(checkedPorts);
                startPortScanning(list);
            }
            break;
            default: {
                Utils.hideKeyboard(getContext().getApplicationContext(), inputPort);
                String ports = (String) v.getTag();
                ArrayList<Integer> list = Utils.convertStringToIntegerList(ports);
                startPortScanning(list);
            }
        }
    }

    private void startPortScanning(ArrayList<Integer> pList) {
        if (pList.size() == 0) {
            return;
        }
        scanProgressBar.setVisibility(View.VISIBLE);
        setResultsRed(pList);
        sScanId++;
        mListener.onStartScan(pList, MainActivity.strLastQuery, sScanId);
    }



    private void setResultsRed(ArrayList<Integer> pList) {
        StringBuilder result = new StringBuilder();
        for (Integer num : pList) {
            Utils.appendRedText(result, num);
        }
        tvResult.setText(Html.fromHtml(result.toString()));
    }

    // Called in Android UI's main thread
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEachPortScanResult(PortScanFinishEvent event) {
        if (event.isListScanFinished) {
            scanProgressBar.setVisibility(View.GONE);
        }

        if (event.host.equals(MainActivity.strLastQuery)) {
            setResults(event.scanResults);
        }
    }

    private void setResults(SparseIntArray results) {

        StringBuilder result = new StringBuilder();

        int firstRangeNum = 0;

        for (int i = 0; i < results.size(); i++) {
            int port = results.keyAt(i); // port num
            int nextPort = 0;
            boolean isNextPortOpen = false;
            if (i + 1 < results.size()) {
                nextPort = results.keyAt(i + 1);
                isNextPortOpen = results.get(nextPort) == PortScanRunnable.OPEN;
            }
            // get the object by the key.
            int state = results.get(port); // state of port

            if (state == PortScanRunnable.OPEN) {
                if (isNextPortOpen && nextPort - port == 1) {
                    if (firstRangeNum == 0) {
                        firstRangeNum = port;
                    }
                } else {
                    if (firstRangeNum == 0) {
                        Utils.appendGreenText(result, port);
                    } else {
                        Utils.appendGreenText(result, "" + firstRangeNum + "-" + port);
                        firstRangeNum = 0;
                    }
                }
            } else {
                if (!isNextPortOpen && nextPort - port == 1) {
                    if (firstRangeNum == 0) {
                        firstRangeNum = port;
                    }
                } else {
                    if (firstRangeNum == 0) {
                        Utils.appendRedText(result, port);
                    } else {
                        Utils.appendRedText(result, "" + firstRangeNum + "-" + port);
                        firstRangeNum = 0;
                    }
                }

            }

        }

        tempLastScanResult = result.toString();
        tvResult.setText(Html.fromHtml(tempLastScanResult));
    }

    public void refresh() {
        MyLog.d("refresh");
        tvStatus.setText("");
        pingProgressBar.setVisibility(View.VISIBLE);
        btnRePing.setVisibility(View.INVISIBLE);
        start();
    }

    private void start() {
        tvQuery.setText(MainActivity.strLastQuery);
        checkAndPing();
    }

    // TextWatcher
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > 0) {
            validatePort();
        }
    }
    // ---------

    private boolean validatePort() {
        // TODO add input format validation
        String inputText = inputPort.getText().toString().trim();
        if (inputText.isEmpty()) {
            inputLayoutPort.setError(getString(R.string.enter_custom_ports));
            requestFocus(inputPort);
            return false;
        } else {
            inputLayoutPort.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    // PING
    private void checkAndPing() {
        if (Utils.isNetworkAvailable(getContext().getApplicationContext())) {
            new Thread(new PingRunnable(MainActivity.strLastQuery, new PingRunnable.CallBack() {
                @Override
                public void onResult(String strHost, boolean pingResult) {
                    sPingResult = pingResult;
                    if (isVisible()) {
                        getActivity().runOnUiThread(onPingResult);
                    }
                }
            })).start();
        } else {
            MyLog.d("Network not available");
            Snackbar.make(tvQuery, R.string.network_not_available, Snackbar.LENGTH_SHORT).show();
        }
    }

    private Runnable onPingResult = new Runnable() {
        @Override
        public void run() {
            pingProgressBar.setVisibility(View.GONE);
            btnRePing.setVisibility(View.VISIBLE);
            tvStatus.setText(sPingResult ? getString(R.string.success) : getString(R.string.fail));
            tvStatus.setTextColor(sPingResult ? getResources().getColor(R.color.colorAccent) : Color.RED);
        }
    };

    // Buttons creation
    private void populateBtns(Cursor cursor) {
        try {
            while (cursor.moveToNext()) {
                String title = cursor.getString(COL_TITLE);
                String ports = cursor.getString(COL_PORTS);
                createAndAddButton(title, ports);
            }
        } finally {
            cursor.close();
        }
    }

    private void createAndAddButton(String title, String ports) {
        numOfButtons++;
        if (numOfButtons % 3 == 1) {
            tempLayout = createLinearLayout();
        }

        LayoutInflater inflater = getActivity().getLayoutInflater();
        Button btn = (Button) inflater.inflate(R.layout.button, null);
        btn.setLayoutParams(btnParams);
        btn.setText(title);
        btn.setTag(ports);
        btn.setOnClickListener(this);
        tempLayout.addView(btn);

    }

    private LinearLayout createLinearLayout() {
        LinearLayout linLayout = new LinearLayout(getActivity());
        linLayout.setOrientation(LinearLayout.HORIZONTAL);
        linLayout.setGravity(Gravity.CENTER);
        btnsLayout.addView(linLayout, layuotParams);
        return linLayout;
    }

    // Loader methods
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case BUTTONS_LOADER:
                return new CursorLoader(getActivity(), DbContract.ButtonsEntry.CONTENT_URI, null, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        MyLog.d("onLoadFinished (BUTTONS_LOADER)");
        populateBtns(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }




    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */


    public interface ScanFragmentInteractionListener {
        void onStartScan(ArrayList<Integer> pList, String host, int scanId);
    }


}
