package net.alexandroid.network.portwatcher.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
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

import net.alexandroid.network.portwatcher.R;
import net.alexandroid.network.portwatcher.data.DbContract;
import net.alexandroid.network.portwatcher.helpers.MyLog;
import net.alexandroid.network.portwatcher.helpers.Utils;
import net.alexandroid.network.portwatcher.task.Ping;
import net.alexandroid.network.portwatcher.ui.activities.MainActivity;
import net.alexandroid.network.portwatcher.ui.activities.ResultActivity;


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

    public static final int COL_ID = 0;
    public static final int COL_TITLE = 1;
    public static final int COL_PORTS = 2;

    private static final int BUTTONS_LOADER = 1;
    private static boolean sPingResult;

    private int numOfButtons;

    private TextView tvQuery, tvStatus;
    private ProgressBar progressBar;
    private TextInputLayout inputLayoutPort;
    private EditText inputPort;
    private ImageView btnRePing;
    private Button btnScan;
    private LinearLayout btnsLayout, tempLayout;

    private LinearLayout.LayoutParams btnParams, layuotParams;
    //private OnListOfMainFragmentInteractionListener mListener;

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
        start();
        return view;
    }

    private void setMyParams() {
        layuotParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layuotParams.topMargin = Utils.getDpInPixels(10) * -1;
        btnParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnParams.weight = 1;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(BUTTONS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getLoaderManager().restartLoader(BUTTONS_LOADER, null, this);
    }

    private void setViews(View v) {
        tvQuery = (TextView) v.findViewById(R.id.tvQuery);
        tvStatus = (TextView) v.findViewById(R.id.tvStatus);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
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
        switch (v.getId()) {
            case R.id.btnRePing:
                if (Utils.isNetworkAvailable(getContext().getApplicationContext())) {
                    tvStatus.setText("");
                    progressBar.setVisibility(View.VISIBLE);
                    btnRePing.setVisibility(View.INVISIBLE);
                    checkAndPing();
                } else {
                    // TODO Show connection problem message
                }
                break;
            case R.id.btnScan:
                if (Utils.isNetworkAvailable(getContext().getApplicationContext())) {

                    validatePort();
                    // TODO Add scan code

                    //startActivity(new Intent(getActivity(), ResultActivity.class));
                } else {
                    // TODO Show connection problem message
                }
                break;
        }
    }

    public void refresh() {
        MyLog.d("refresh");
        tvStatus.setText("");
        progressBar.setVisibility(View.VISIBLE);
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
        if(s.length() > 0) {
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
            new Thread(new Ping(MainActivity.strLastQuery, new Ping.CallBack() {
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
            // TODO Show message: network connection problem
        }
    }

    private Runnable onPingResult = new Runnable() {
        @Override
        public void run() {
            progressBar.setVisibility(View.GONE);
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
        btn.setText(title);
        btn.setTag(ports);
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
        MyLog.d("onLoadFinished");
        populateBtns(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


/*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListOfMainFragmentInteractionListener) {
            mListener = (OnListOfMainFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    *//**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *//*
    public interface OnListOfMainFragmentInteractionListener {
        void onItemClick(ScanItem item);
    }*/
}
