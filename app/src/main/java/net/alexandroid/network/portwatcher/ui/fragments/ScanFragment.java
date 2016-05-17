package net.alexandroid.network.portwatcher.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.alexandroid.network.portwatcher.R;
import net.alexandroid.network.portwatcher.helpers.MyLog;
import net.alexandroid.network.portwatcher.task.Ping;
import net.alexandroid.network.portwatcher.ui.activities.MainActivity;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@lin OnListOfMainFragmentInteractionListener}
 * interface.
 */
public class ScanFragment extends Fragment implements
        View.OnClickListener,
        TextWatcher {

    private static boolean sPingResult;

    private TextView tvQuery, tvStatus;
    private ProgressBar progressBar;
    private TextInputLayout inputLayoutPort;
    private EditText inputPort;
    private ImageView btnRePing;
    private Button btnScan;
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
        setViews(view);
        setListeners();
        start();
        return view;
    }

    private void setViews(View v) {
        tvQuery = (TextView) v.findViewById(R.id.tvQuery);
        tvStatus = (TextView) v.findViewById(R.id.tvStatus);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        inputLayoutPort = (TextInputLayout) v.findViewById(R.id.input_layout_port);
        inputPort = (EditText) v.findViewById(R.id.input_port);
        btnRePing = (ImageView) v.findViewById(R.id.btnRePing);
        btnScan = (Button) v.findViewById(R.id.btnScan);
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
                tvStatus.setText("");
                progressBar.setVisibility(View.VISIBLE);
                btnRePing.setVisibility(View.INVISIBLE);
                checkAndPing();
                break;
            case R.id.btnScan:
                // TODO Add scan code
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
        validatePort();
    }
    // ---------


/*    public void onFabClick() {
        validatePort();
    }*/

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
        // TODO Add internet connection check

        new Thread(new Ping(MainActivity.strLastQuery, new Ping.CallBack() {
            @Override
            public void onResult(String strHost, boolean pingResult) {
                sPingResult = pingResult;
                if (isVisible()) {
                    getActivity().runOnUiThread(onPingResult);
                }
            }
        })).start();
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
